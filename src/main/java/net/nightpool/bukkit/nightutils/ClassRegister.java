package net.nightpool.bukkit.nightutils;

import java.lang.reflect.InvocationTargetException;

public class ClassRegister<T> extends Registar<Class<? extends T>> {
	
	public ClassRegister(IErrorsPlugin p) {
		super(p);
	}


	public void register(Class<? extends T> cls) {
		Registerable an = cls.getAnnotation(Registerable.class);
		if(an==null){
			p.getLogger().warning("Class "+cls.getSimpleName()+" is improperly formated. Skipping");
			return;
		}
		register(an.name(), cls);
		if(an.aliases()!=new String[]{""}){
			for(String i : an.aliases()){
				if(map.get(i)==null){
					registerAlias(i, an.name());
				}
			}
		}
	}
	

	public T instantiate(Class<? extends T> x, Object... obs) throws Exception{
		
		Class<?>[] clss = new Class<?>[obs.length];
		
		int i =0;
		for(Object o : obs){
			clss[i]=o.getClass();
			i++;
		}
		
		try {
			T newI = x.getConstructor(clss).newInstance(obs);
			return newI;
		} catch (IllegalArgumentException e) {
			p.logError(e);
		} catch (SecurityException e) {
			p.logError(e);
		} catch (InstantiationException e) {
			p.logError(e);
		} catch (IllegalAccessException e) {
			p.logError(e);
		} catch (InvocationTargetException e) {
			if(e.getCause() instanceof Exception){
				throw (Exception) e.getCause();
			} else{
				if(e.getCause() != null){
					p.logError(e.getCause());
				}else{
					p.logError(e);
				}
			}
		}
		return null;
	}
	
	public T instantiate(String name, Object... obs) throws Exception{
		Class<? extends T> x = get(name, true);
		if(x==null){
			throw new ClassNotFoundException();
		}
		return instantiate(x, obs);
	}
}