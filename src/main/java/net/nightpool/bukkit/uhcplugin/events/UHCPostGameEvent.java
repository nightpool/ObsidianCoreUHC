package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPostGameEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UHCGame game;
    private String winString;

    public UHCPostGameEvent(UHCGame uhcGame, String winString) {
        this.game = uhcGame;
        this.winString = winString;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public UHCGame getGame() {
        return game;
    }

    public String getWinningPlayer() {
        return winString;
    }

    public void setWinningPlayer(String winString) {
        this.winString = winString;
    }

}
