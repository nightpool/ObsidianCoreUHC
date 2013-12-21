package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UHCPlayerLoseEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    protected boolean isCancelled;
    private UHCGame game;
    
    public Player player;
    public PlayerDeathEvent deathEvent;

    public UHCPlayerLoseEvent(UHCGame game, PlayerDeathEvent ev) {
        this.deathEvent = ev;
        this.player = ev.getEntity();
        this.game = game;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public UHCGame getGame() {
        return game;
    }
    

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
