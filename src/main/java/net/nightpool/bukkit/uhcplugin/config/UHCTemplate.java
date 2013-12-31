package net.nightpool.bukkit.uhcplugin.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import net.nightpool.bukkit.nightutils.GeneralConfig;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;

public class UHCTemplate extends GeneralConfig{
    
    public int timerIntervals;
    
    public boolean manualPlayers;
    
    public List<String> rulesets;
    public String name;
    
    public Map<String, SubConfig> subconfigs;
    
    
    public UHCTemplate(UHCPlugin p, File file, String template, String name) {
        super(p, file, template);
        this.name = name;
    }
    
    public SubConfig getSubConfig(String key){
        return subconfigs.get(key);
    }

    @Override
    protected MemorySection toConfig(MemorySection config) {
        config.set("timer-intervals", timerIntervals);
        
        config.set("manual-players", manualPlayers);
        
        config.set("rulesets", rulesets);
        
        for(Map.Entry<String, SubConfig> i : subconfigs.entrySet()){
            ConfigurationSection s = config.createSection(i.getKey());
            i.getValue().toConfig(s);
        }
        return config;
    }

    @Override
    protected void fromConfig(MemorySection config) {
        timerIntervals = config.getInt("timer-intervals");

        manualPlayers = config.getBoolean("manual-players");
        
        rulesets = config.getStringList("rulesets");
        
        subconfigs = new HashMap<String, SubConfig>();
        for(String i : rulesets){
            Class<? extends UHCRuleset> r = p.getRuleset(i);
            if(r==null){
                p.getLog().warning("Invalid ruleset "+i+". Skipping");continue;
            }
            Class<RulesetConfig> a_class = UHCRuleset.RulesetConfig.class;
            if (r.isAnnotationPresent(a_class)){
                RulesetConfig a = r.getAnnotation(a_class);
                if(!config.isConfigurationSection(a.key())){
                    config.createSection(a.key());
                }
                
                try {
                    SubConfig s;
//                    if (a.config().getDeclaringClass() != null){
//                        s = a.config().getConstructor(r.getClass(), ConfigurationSection.class)
//                                .newInstance(r, config.getConfigurationSection(a.key()));
//                    } else{
//                    }
                    s = a.config().getConstructor(ConfigurationSection.class)
                            .newInstance(config.getConfigurationSection(a.key()));   
                    subconfigs.put(a.key(), s);
                } catch (Exception e){
                    p.logError(e); continue;
                }
            }
        }
    }

     public static Map<Double, Integer> toDubIntMap(List<Map<?, ?>> mapList, String mapname) {
        Map<Double, Integer> map = new HashMap<Double, Integer>();
        for (Map<?, ?> m : mapList){
            if(m.size() > 1){
                return map;
            }
            Object db = m.keySet().toArray()[0];
            Object ib = m.values().toArray()[0];
            Double d;
            Integer i;
            if (db instanceof Double){
//                Bukkit.getLogger().info(UHCPlugin.logPrefix+"Double: "+((Double)db).toString());
                d = (Double)db;
            } else if (db instanceof Integer){
//                Bukkit.getLogger().info(UHCPlugin.logPrefix+"Integer: "+((Integer)db).toString());
                d = ((Integer) db).doubleValue();
            } else{
//                Bukkit.getLogger().warning(UHCPlugin.logPrefix+"Error parsing key in "+db.toString()+": "+ib.toString()+" in "+mapname+" map. Skipping");
                continue;
            }
            if (ib instanceof Integer){
//                Bukkit.getLogger().info(UHCPlugin.logPrefix+"Integer: "+((Integer)ib).toString());
                i = ((Integer) ib);
            }else{
//                Bukkit.getLogger().warning(UHCPlugin.logPrefix+"Error parsing value in "+db.toString()+": "+ib.toString()+" in "+mapname+" map. Skipping");
                continue;
            }
            map.put(d, i);
        }
        return map;
    }
    
}
