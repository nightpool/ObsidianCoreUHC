package net.nightpool.bukkit.uhcplugin.events;

import java.util.List;

import net.nightpool.bukkit.uhcplugin.game.UHCGame;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class UHCPlayerLoseEvent extends PlayerDeathEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

	protected boolean isCancelled;
	private UHCGame game;

	public UHCPlayerLoseEvent(UHCGame game, PlayerDeathEvent ev) {
		super(ev.getEntity(), ev.getDrops(), ev.getDroppedExp(), ev.getNewExp(), ev.getNewTotalExp(), ev.getNewLevel(), ev.getDeathMessage());
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
	
	public void overwriteDeathEvent(PlayerDeathEvent ev){
		ev.setDeathMessage(getDeathMessage());
		ev.setDroppedExp(getDroppedExp());
		ev.setKeepLevel(getKeepLevel());
		ev.setNewExp(getNewExp());
		ev.setNewLevel(getNewLevel());
		ev.setNewTotalExp(getNewTotalExp());
		List<ItemStack> d = ev.getDrops();
		d.clear();
		d.addAll(getDrops());
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
