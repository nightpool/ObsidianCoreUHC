package net.nightpool.bukkit.uhcplugin.events;

import net.nightpool.bukkit.uhcplugin.UHCGame;

import org.bukkit.entity.Player;
import org.bukkit.event.*;


public class UHCGameOverEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();


	private boolean isCancelled = false;
	private UHCGame game;
	private Player winningPlayer;
	

	public UHCGameOverEvent(UHCGame uhcGame, Player winningPlayer) {
		this.game = uhcGame;
		this.winningPlayer = winningPlayer;
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

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public void setWinningPlayer(Player winningPlayer) {
		this.winningPlayer = winningPlayer;
	}

}
