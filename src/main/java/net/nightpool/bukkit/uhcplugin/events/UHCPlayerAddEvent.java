package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPlayerAddEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

	private boolean isCancelled = false;

	private OfflinePlayer player;

	private UHCGame game;

	private String reason;


	public UHCPlayerAddEvent(UHCGame game, OfflinePlayer opl) {
		this.player = opl;
		this.game = game;
		this.reason = null;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer opl) {
		this.player = opl;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancel = isCancelled;
	}
	
	public void setCancelled(boolean cancel, String reason) {
		cancel = isCancelled;
		this.reason = reason;
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

	public String getReason() {
		return reason;
	}
	public void setReason(String reason){
		this.reason=reason;
	}

}
