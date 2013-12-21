package net.nightpool.bukkit.uhcplugin.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.nightpool.bukkit.nightutils.GeneralConfig;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;

public abstract class SubConfig{

    protected Map<String, Integer> ints;
    protected Map<String, Integer> intDefaults;
    protected Map<String, Boolean> bools;
    protected Map<String, Boolean> boolDefaults;
    protected Map<String, Double> doubles;
    protected Map<String, Double> doubleDefaults;
    protected Map<String, String> strings;
    protected Map<String, String> stringDefaults;
    

    protected Map<String, Map<Double, Integer>> dubIntMaps;
    protected Map<String, Map<Double, Integer>> dubIntMapsDefaults;
    
    public SubConfig(ConfigurationSection fromSection) {
        throw new NotImplementedException();
    }
    
    protected SubConfig(){
        ints = new HashMap<String, Integer>();
        intDefaults = new HashMap<String, Integer>();
        bools = new HashMap<String, Boolean>();
        boolDefaults = new HashMap<String, Boolean>();
        doubles = new HashMap<String, Double>();
        doubleDefaults = new HashMap<String, Double>();
        strings = new HashMap<String, String>();
        stringDefaults = new HashMap<String, String>();
        dubIntMaps = new HashMap<String, Map<Double, Integer>>();
        dubIntMapsDefaults = new HashMap<String, Map<Double, Integer>>();
    }
    
    protected void addInt(String key, int def){
        ints.put(key, def);
        intDefaults.put(key, def);
    }
    public int getInt(String key){
        return ints.get(key);
    }
    protected void addBool(String key, boolean def){
        bools.put(key, def);
        boolDefaults.put(key, def);
    }
    public boolean getBool(String key){
        return bools.get(key);
    }
    
    protected void addDouble(String key, double def){
        doubles.put(key, def);
        doubleDefaults.put(key, def);
    }
    public double getDouble(String key){
        return doubles.get(key);
    }
    
    protected void addString(String key, String def){
        strings.put(key, def);
        stringDefaults.put(key, def);
    }

    public String getString(String key){
        return strings.get(key);
    }

    protected void addDubIntMap(String key, Map<Double, Integer> def) {
        dubIntMaps.put(key, def);
        dubIntMapsDefaults.put(key, def);
    }

    public Map<Double, Integer> getDubIntMap(String key){
        return dubIntMaps.get(key);
    }
    
    public void fromConfig(ConfigurationSection config) {
        for(Entry<String, Integer> i : intDefaults.entrySet()){
            ints.put(i.getKey(), config.getInt(i.getKey(), i.getValue()));
        }
        for(Entry<String, Boolean> i : boolDefaults.entrySet()){
            bools.put(i.getKey(), config.getBoolean(i.getKey(), i.getValue()));
        }
        for(Entry<String, Double> i : doubleDefaults.entrySet()){
            doubles.put(i.getKey(), config.getDouble(i.getKey(), i.getValue()));
        }
        for(Entry<String, String> i : stringDefaults.entrySet()){
            strings.put(i.getKey(), config.getString(i.getKey(), i.getValue()));
        }
        for(Entry<String, Map<Double, Integer>> i : dubIntMapsDefaults.entrySet()){
            config.addDefault(i.getKey(), GeneralConfig.toMapList(i.getValue()));
            dubIntMaps.put(i.getKey(), UHCTemplate.toDubIntMap(config.getMapList(i.getKey()), i.getKey()));
        }
    }
    
    public ConfigurationSection toConfig(ConfigurationSection config) {
        char s = config.getRoot().options().pathSeparator();
        config.getRoot().options().pathSeparator('@');
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.putAll(ints);
        temp.putAll(bools);
        temp.putAll(doubles);
        temp.putAll(strings);
        for(Entry<String, Map<Double, Integer>> i : dubIntMaps.entrySet()){
            ArrayList<Map<Double, Integer>> mapList = GeneralConfig.toMapList(i.getValue());
            temp.put(i.getKey(), mapList);
        }
        for(Entry<String, Object> i : temp.entrySet()){
            config.set(i.getKey(), i.getValue());
        }
        config.getRoot().options().pathSeparator(s);
        return config;
    }
}
