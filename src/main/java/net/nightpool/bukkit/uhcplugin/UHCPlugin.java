package net.nightpool.bukkit.uhcplugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import net.nightpool.bukkit.nightutils.CommandPlugin;
import net.nightpool.bukkit.uhcplugin.commands.UHCCommandRegister;
import net.nightpool.bukkit.uhcplugin.config.UHCMainConfig;
import net.nightpool.bukkit.uhcplugin.config.UHCTemplate;
import net.nightpool.bukkit.uhcplugin.game.UHCGame;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset;

public class UHCPlugin extends CommandPlugin{
	
	public class GameNotRunningException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static final boolean debug = true;
	public static final String log_prefix = "["+"ObsidianCoreUHC"+"] ";
	public static final String fancyName = "§5§lO§r§5bsidian§lC§r§5oreUHC";
	public static final String chat_prefix = ChatColor.DARK_PURPLE+log_prefix+ChatColor.LIGHT_PURPLE;
	public Logger log;	
	private UHCGame game;
	
	UHCMainConfig config;
	Map<String, UHCTemplate> templates;
	public Map<String, Class<? extends UHCRuleset>> rulesets;
	
	UHCCommandRegister commandRegister;

	public PluginDescriptionFile pdf;
	

	
	//Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()

	@Override
	public void onEnable() {
        pdf = getDescription();
		log = getLogger();
		
		game = null;
		commandRegister = new UHCCommandRegister(this);
		getCommand("uhc").setExecutor(commandRegister);

        load_config();
		
		
        Bukkit.getConsoleSender().sendMessage(fancyName+" version " + pdf.getVersion() + " enabled!");
	}
	
	private void load_config() {
		File folder = this.getDataFolder();
        if(!folder.exists()){folder.mkdir();}
		config = new UHCMainConfig(this, new File(folder, "config.yml"));
		load_rulesets(); // needs to be after main config, but before templates 
		File template_folder = new File(folder, "templates/");
		if(!template_folder.exists()){
			template_folder.mkdir();
			File outFile = new File(template_folder, "default.yml");
			this.templates = new HashMap<String, UHCTemplate>();
			templates.put("default", new UHCTemplate(this, outFile, "template.yml", "default"));
		}else{
			reload_templates();
		}
	}

	private void load_rulesets() {
		rulesets = new HashMap<String, Class<? extends UHCRuleset>>();
		File ruleset_folder = new File(this.getDataFolder(), "rulesets/");
		if(!ruleset_folder.exists()){ruleset_folder.mkdir();}
		File[] ruleset_files = ruleset_folder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				String[] x = arg1.split(".");
				if (x.length < 2){
					return true;
				} else{
					return x[x.length-1] == "jar";	
				}
			}
		});
		URL[] jar_urls = new URL[ruleset_files.length];
		for(int i = 0; i<ruleset_files.length; i++){
			try {
				jar_urls[i] = ruleset_files[i].toURI().toURL();
			} catch (MalformedURLException e) {log.severe("Yeah nope. Your filesystem's WAY to screwy for my hotness.");return;}
		}
		URLClassLoader ruleset_loader = new URLClassLoader(jar_urls, this.getClassLoader());
		for(String key : config.rulesets.keySet()){
			String x = config.rulesets.get(key);
			try {
				Class<?> r = ruleset_loader.loadClass(x);
				rulesets.put(key, r.asSubclass(UHCRuleset.class));
				if(debug){
					log.info("Adding ruleset "+key+" with class "+x);
				}
			} catch (ClassNotFoundException e) {
				log.warning("Class "+x+" for ruleset "+key+" not found. Skipping.");
			} catch (ClassCastException e){
				log.warning("Class "+x+" for ruleset "+key+" does not descend from UHCRuleset. Skipping.");
			}
		}
		try {
			ruleset_loader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, UHCTemplate> reload_templates() {
		this.templates = new HashMap<String, UHCTemplate>();
		File template_folder = new File(this.getDataFolder(), "templates/");
		for(File i : template_folder.listFiles()){
			String name = i.getName().split("\\.")[0];
			templates.put(name, new UHCTemplate(this, i, "template.yml", name));
		}
		return this.templates;
	}

	public boolean gameRunning(){
		return game!=null;
	}
	
	public UHCGame getGame(){
		return game;
	}

	public void gameOver() {
		this.game = null;
	}
	
	public Map<String, UHCTemplate> getTemplates(){
		return templates;
	}
	
	public UHCMainConfig getMainConfig(){
		return config;
	}
	
	@Override
	public UHCCommandRegister getCommandRegister(){
		return commandRegister;
	}

	@Override
	public void onDisable() {
		log.info("Saving global config...");
    	config.save();
    	for(Entry<String, UHCTemplate> i : templates.entrySet()){
    		log.info("Saving config for "+i.getKey()+" template");
    		i.getValue().save();
    	}
    	log.info("Disabled version " + pdf.getVersion());
	}

	public Class<? extends UHCRuleset> getRuleset(String i) {
		return rulesets.get(i);
	}

	public void broadcast(String message, Set<CommandSender> players) {
		for(CommandSender i : players){
			i.sendMessage(chat_prefix + message);
		}
	}
	public void broadcast(String message, String permission){
		Bukkit.broadcast(chat_prefix + message, permission);
	}
	
	public void broadcast(String message){
		Bukkit.broadcastMessage(chat_prefix + message);
	}

	public void loadGame(UHCTemplate t, CommandSender sender, World world) {
		this.game = new UHCGame(t, this, world, sender);
	}
	
}
