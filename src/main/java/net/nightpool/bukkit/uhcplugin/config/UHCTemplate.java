package net.nightpool.bukkit.uhcplugin.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import net.nightpool.bukkit.nightutils.GeneralConfig;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.UHCRuleset;
import net.nightpool.bukkit.uhcplugin.UHCRuleset.RulesetConfig;

public class UHCTemplate extends GeneralConfig{
	
	public int timerIntervals;	
	public boolean specAllowed;
	public boolean specCanJoin;	
	public String specPrefix;	
	
	public boolean manualPlayers;

	public String scatter;
	
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

		config.set("spectators.allowed", specAllowed);
		config.set("spectators.can-join", specCanJoin);
		config.set("spectators.prefix", specPrefix);
		
		config.set("manual-players", manualPlayers);
		
		config.set("scatter", scatter);
		
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

		specAllowed = config.getBoolean("spectators.allowed");
		specCanJoin = config.getBoolean("spectators.can-join");
		specPrefix = config.getString("spectators.prefix");
		
		manualPlayers = config.getBoolean("approve-players");
		
		scatter = config.getString("scatter");
		
		rulesets = config.getStringList("rulesets");
		
		subconfigs = new HashMap<String, SubConfig>();
		for(String i : rulesets){
			Class<? extends UHCRuleset> r = p.getRuleset(i);
			if(r==null){
				p.log.warning("Invalid ruleset "+i+". Skipping");continue;
			}
			Class<RulesetConfig> a_class = UHCRuleset.RulesetConfig.class;
			if (r.isAnnotationPresent(a_class)){
				RulesetConfig a = r.getAnnotation(a_class);
				if(!config.isConfigurationSection(a.configKey())){
					config.createSection(a.configKey());
				}
				
				try {
					SubConfig s = a.subConfigClass().getConstructor(ConfigurationSection.class)
							.newInstance(config.getConfigurationSection(a.configKey()));
					subconfigs.put(a.configKey(), s);
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
//				p.logError("Bad configuration. Check your "+mapname+" config.", new Exception());
				return map;
			}
			Object db = m.keySet().toArray()[0];
			Object ib = m.values().toArray()[0];
			Double d;
			Integer i;
			if (db instanceof Double){
//				p.getLogger().info("Double: "+((Double)db).toString());
				d = (Double)db;
			} else if (db instanceof Integer){
//				p.getLogger().info("Integer: "+((Integer)db).toString());
				d = ((Integer) db).doubleValue();
			} else{
//				p.getLogger().warning("Error parsing key in "+db.toString()+": "+ib.toString()+" in "+mapname+" map. Skipping");
				continue;
			}
			if (ib instanceof Integer){
//				p.getLogger().info("Integer: "+((Integer)ib).toString());
				i = ((Integer) ib);
			}else{
//				p.getLogger().warning("Error parsing value in "+db.toString()+": "+ib.toString()+" in "+mapname+" map. Skipping");
				continue;
			}
			map.put(d, i);
		}
		return map;
	}
	
}
