package net.nightpool.bukkit.nightutils;

import java.util.logging.Logger;

import net.nightpool.bukkit.uhcplugin.utils.ColorPluginLogger;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ErrorsPlugin extends JavaPlugin implements IErrorsPlugin {

    protected Logger log = null;

    public Logger getLog(){
        if (log == null){
            log = new ColorPluginLogger(this);
        }
        return log;
    }
    
    @Override
    public void logError(Throwable e){
        getLogger().severe(e.toString());
        for(StackTraceElement i : e.getStackTrace()){
            log.severe(i.toString());
        }
    }
    @Override
    public void logError(String message, Throwable e){
        getLogger().severe(message);
        logError(e);
    }
}
