package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.UHCGame;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPlayerRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

	private boolean isCancelled = false;

	private boolean silent;

	private OfflinePlayer player;

	private UHCGame game;


	public UHCPlayerRemoveEvent(UHCGame game, OfflinePlayer opl, boolean silent) {
		this.player = opl;
		this.silent = silent;
		this.game = game;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
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

}
