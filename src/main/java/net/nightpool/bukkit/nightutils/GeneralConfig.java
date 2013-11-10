package net.nightpool.bukkit.nightutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import net.nightpool.bukkit.uhcplugin.UHCPlugin;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class GeneralConfig {

	protected UHCPlugin p;
	protected File file;

	public GeneralConfig(UHCPlugin p, File file, String template) {
		this.p = p;
		this.file = file;
		FileConfiguration config;
		if(!file.exists()){
			try{
				file.createNewFile();
				p.getLogger().warning("RulesetConfig not found. loading "+template+" from jar");
				config=YamlConfiguration.loadConfiguration(p.getResource(template));
			} catch (IOException e){
				p.logError("Could not read default config.",e);
				return;
			}
		} else{
			config=YamlConfiguration.loadConfiguration(file);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			p.logError("Could not save config.", e);
		}
		
		fromConfig(config);
	}
	public GeneralConfig(UHCPlugin p, File file){
		this(p, file, file.getName());
	}
	protected abstract MemorySection toConfig(MemorySection config);
	protected abstract void fromConfig(MemorySection config);
	
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		config=(YamlConfiguration) toConfig(config);
		try {
			config.save(file);
		} catch (IOException e) {
			p.logError("Could not save config.", e);
		}
	}

	public void reload() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		fromConfig(config);
	}
	

	//Utils
	
	public static Map<String, String> toStringMap(List<Map<?,?>> o){
		Map<String, String> ret = new HashMap<String, String>();
		for(Map<?, ?> i : o){
			for(Entry<?,?> j : i.entrySet()){
				ret.put(j.getKey().toString(), j.getValue().toString());
			}
		}
		return ret;
	}
	
	public static <T, U> ArrayList<Map<T, U>> toMapList(Map<T, U> map) {
		ArrayList<Map<T, U>> r_list = new ArrayList<Map<T, U>>();
		for(Map.Entry<T,U> i: map.entrySet()){
			HashMap<T, U> h = new HashMap<T, U>();
			h.put(i.getKey(), i.getValue());
			r_list.add(h);
		}
		return r_list;
	}
	
	public List<String> toStringList(Object o){
		List<String> ret = new ArrayList<String>();
		if (o instanceof List<?>){
			for(Object i : (List<?>) o){
				if ((i instanceof String) || (isPrimitiveWrapper(i))) {
		            ret.add(String.valueOf(i));
		        }
			}
		} else if ((o instanceof String) || (isPrimitiveWrapper(o))) {
            ret.add(String.valueOf(o));
        } else if( o instanceof MemorySection){
        	MemorySection memsec = (MemorySection) o;
        	Set<String> x = memsec.getKeys(false);
        	for(String i : x){
        		ret.addAll(toStringList(memsec.get(i)));
        	}
        } else if(o instanceof Map){
        	for(Map.Entry<?, ?> i : ((Map<?, ?>) o).entrySet()){
        		if((i.getKey() instanceof String) || (isPrimitiveWrapper(i.getKey()))){
        			if((i.getValue() instanceof String) || (isPrimitiveWrapper(i.getValue()))){
            			ret.add(String.valueOf(i.getKey()+": "+i.getValue()));
            		}
        		}
        	}
        }
		return ret;
	}
	
	
	
    public static boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }
    
    public Map<String, Object> toMap(){
    	MemorySection config = this.toConfig(new YamlConfiguration());
    	return config.getValues(true);
    }
}
