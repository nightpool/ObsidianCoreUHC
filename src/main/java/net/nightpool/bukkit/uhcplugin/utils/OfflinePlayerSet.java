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
public class OfflinePlayerSet extends AbstractSet<OfflinePlayer> implements Set<OfflinePlayer>, ConfigurationSerializable {

    private HashMap<String, Boolean> map;
    
    public OfflinePlayerSet(){
        map = new HashMap<String, Boolean>();
    }
    
    public OfflinePlayerSet(Collection<OfflinePlayer> c){
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }
    
    public OfflinePlayerSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    public OfflinePlayerSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }
    @Override
    public Iterator<OfflinePlayer> iterator() {
        return new PlayerIterator(map.keySet().iterator());
    }
    
    public Iterator<String> name_iterator(){
        return map.keySet().iterator();
    }
    
    @Override
    public boolean add(OfflinePlayer pl){
        if(contains(pl)){return false;}
        map.put(pl.getName().toLowerCase(), true);
        return true;
    }
    
    public boolean remove(OfflinePlayer o) {
        return map.remove(o.getName().toLowerCase()) == true;
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
            return map.containsKey(o);
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
