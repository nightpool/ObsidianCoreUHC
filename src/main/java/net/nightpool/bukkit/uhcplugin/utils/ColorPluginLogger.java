package net.nightpool.bukkit.uhcplugin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

public class ColorPluginLogger extends PluginLogger {

    public final static Map<Level, ChatColor> color_map = new HashMap<Level, ChatColor>();
    static{
        color_map.put(Level.SEVERE, ChatColor.RED);
        color_map.put(Level.WARNING, ChatColor.YELLOW);
    }
    
    public ColorPluginLogger(Plugin context) {
        super(context);
    }
    
    @Override
    public void log(LogRecord logRecord) {
        Level level = logRecord.getLevel();
        logRecord.setMessage((color_map.containsKey(level)? color_map.get(level) : "") + logRecord.getMessage());
        super.log(logRecord);
    }
}
