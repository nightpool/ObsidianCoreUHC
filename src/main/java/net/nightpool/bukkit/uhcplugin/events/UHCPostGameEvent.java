package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPostGameEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UHCGame game;
    private Player winningPlayer;

    public UHCPostGameEvent(UHCGame uhcGame, Player winningPlayer) {
        this.game = uhcGame;
        this.winningPlayer = winningPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public UHCGame getGame() {
        return game;
    }

    public Player getWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(Player winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

}
