package net.nightpool.bukkit.nightutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Registar<T> {
	IErrorsPlugin p;
	Map<String, T> map = new HashMap<String, T>();
	Map<String, String> aliases = new HashMap<String, String>();
	
	public Registar(IErrorsPlugin p){
		this.p = p;
	}
	
	public Map<String, T> getAll(){
		return map;
	}
	
	public T get(String s){
		return get(s, true);
	}
	
	public T get(String s, boolean resolveAlias){
		T item = map.get(s);
		if(item!=null){
			return item;
		} else if(resolveAlias){
			return map.get(resolveAlias(s));
		} else{
			return null;
		}
	}
	
	public List<String> getAliases(String s){
		List<String> ret= new ArrayList<String>();
		for(Map.Entry<String, String> i : aliases.entrySet()){
			if(i.getValue().equals(s)){
				ret.add(i.getKey());
			}
		}
		return ret;
	}
	
	public Map<String, String> getAliases(){
		return aliases;
	}
	
	public String resolveAlias(String s){
		return aliases.get(s);
	}
	
	public void registerAlias(String k, String v){
		registerAlias(k,v,false);
	}
	
	public void registerAlias(String k, String v, boolean override){
		if(!map.containsKey(k) || override){
			aliases.put(k, v);
		}
	}
	
	public void register(String s, T item){
		map.put(s, item);
	}
	
	public void remove(String s){
		map.remove(s);
		
		for(Map.Entry<String, String> i: aliases.entrySet()){
			if(i.getValue().equals(s)){
				aliases.remove(i.getKey());
			}
		}
	}
	
	public void removeAlias(String s){
		aliases.remove(s);
	}
}