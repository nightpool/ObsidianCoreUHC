package net.nightpool.bukkit.nightutils;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ErrorsPlugin extends JavaPlugin implements IErrorsPlugin {

    
    @Override
    public void logError(Throwable e){
        getLogger().severe(e.toString());
        for(StackTraceElement i : e.getStackTrace()){
            getLogger().severe(i.toString());
        }
    }
    @Override
    public void logError(String message, Throwable e){
        getLogger().severe(message);
        logError(e);
    }
}
