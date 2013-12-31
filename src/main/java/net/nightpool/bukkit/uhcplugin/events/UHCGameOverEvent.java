package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.event.*;


public class UHCGameOverEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();


    private boolean isCancelled = false;
    private UHCGame game;
    private String winString;
    

    public UHCGameOverEvent(UHCGame uhcGame, String winString) {
        this.game = uhcGame;
        this.winString = winString;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancel = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UHCGame getGame() {
        return game;
    }

    public void setGame(UHCGame game) {
        this.game = game;
    }

    public String getWinString() {
        return winString;
    }

    public void setWinningPlayer(String winString) {
        this.winString = winString;
    }

}
