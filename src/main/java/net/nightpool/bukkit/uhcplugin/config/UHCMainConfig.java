package net.nightpool.bukkit.uhcplugin.config;

import java.io.File;
import java.util.Map;

import net.nightpool.bukkit.nightutils.GeneralConfig;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;

import org.bukkit.configuration.MemorySection;

public class UHCMainConfig extends GeneralConfig {

	public Map<String, String> rulesets;
	public boolean debug = false;
	
	public UHCMainConfig(UHCPlugin p, File file) {
		super(p,file);
	}
	
	@Override
	protected MemorySection toConfig(MemorySection config) {
		
		config.set("rulesets", toMapList(rulesets));
		config.set("debug", debug);
		return config;
	}

	@Override
	protected void fromConfig(MemorySection config) {
		debug = config.getBoolean("debug", false);
		rulesets = toStringMap(config.getMapList("rulesets"));
	}

}