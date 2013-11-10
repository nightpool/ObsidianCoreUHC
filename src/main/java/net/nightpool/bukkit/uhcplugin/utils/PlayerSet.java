package net.nightpool.bukkit.uhcplugin.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("UHCPlayerSet")
public class PlayerSet extends AbstractSet<OfflinePlayer> implements Set<OfflinePlayer>, ConfigurationSerializable {

	private HashMap<String, OfflinePlayer> map;
	
	public PlayerSet(){
		map = new HashMap<String, OfflinePlayer>();
	}
	
	public PlayerSet(Collection<OfflinePlayer> c){
		map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
		addAll(c);
	}
	
	public PlayerSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    public PlayerSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }
	@Override
	public Iterator<OfflinePlayer> iterator() {
		return map.values().iterator();
	}
	
	@Override
	public boolean add(OfflinePlayer pl){
		if(contains(pl)){return false;}
		map.put(pl.getName().toLowerCase(), pl);
		return true;
	}
	
	public boolean remove(OfflinePlayer o) {
        return map.remove(o.getName().toLowerCase()) == o;
    }

	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
	public boolean contains(Object o) {
    	if(o instanceof OfflinePlayer){
    		return map.containsKey(((OfflinePlayer) o).getName().toLowerCase());
    	}else{
    		return map.containsValue(o);
    	}
    }
    
    @Override
	public void clear() {
        map.clear();
    }

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> serialize() {
		return (Map<String, Object>) map.clone();
	}

}
