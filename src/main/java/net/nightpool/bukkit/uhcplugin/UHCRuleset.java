package net.nightpool.bukkit.uhcplugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.nightpool.bukkit.uhcplugin.config.SubConfig;

public abstract class UHCRuleset {
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RulesetConfig {
		Class<? extends SubConfig> subConfigClass();
		String configKey();
	}
	
	public UHCPlugin p;
	public UHCGame game;

	public UHCRuleset(UHCGame game, UHCPlugin p) {
		this.game = game;
		this.p = p;
	}
	public abstract void onStart();
	public abstract void onUnLoad();
	
	public SubConfig getConfig(){
		if(this.getClass().isAnnotationPresent(RulesetConfig.class)){
			String key = this.getClass().getAnnotation(RulesetConfig.class).configKey();
			return game.template.getSubConfig(key);
		} else{
			return null;
		}
	}
}
