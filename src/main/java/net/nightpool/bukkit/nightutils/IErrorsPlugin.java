package net.nightpool.bukkit.nightutils;

import org.bukkit.plugin.Plugin;

public interface IErrorsPlugin extends Plugin{

    public abstract void logError(Throwable e);

    public abstract void logError(String message, Throwable e);

}