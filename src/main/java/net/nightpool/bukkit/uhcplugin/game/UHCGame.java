package net.nightpool.bukkit.uhcplugin.game;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.UHCTemplate;
import net.nightpool.bukkit.uhcplugin.events.UHCGameOverEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerAddEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerLoseEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerRemoveEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPostGameEvent;
import net.nightpool.bukkit.uhcplugin.utils.PlayerSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UHCGame implements Listener{

	public boolean running = false;
	public boolean started = false;
	public int countdownTaskId = -1;
	public int timerId = -1;
	
	protected UHCPlugin p;
	public Map<Class<? extends UHCRuleset>, UHCRuleset> rulesets;
	public UHCTemplate template;
	public World world;
	public PlayerSet players;
	public CommandSender runner;
		
	public UHCGame(UHCTemplate template, UHCPlugin p, World world, CommandSender runner){
		this.p = p;
		this.template = template;
		this.world = world;
		players = new PlayerSet();
		if(!template.manualPlayers){
			players.addAll(world.getPlayers());
		}
		p.log.info(""+players);
		this.runner = runner;
		this.rulesets = new HashMap<Class<? extends UHCRuleset>, UHCRuleset>();
		Bukkit.getPluginManager().registerEvents(this, p);
		
		for(String i : template.rulesets){
			if("Default".equalsIgnoreCase(i)){	// Just to make sure that at least the default ruleset always works, no matter what gets messed up.
				if(!rulesets.keySet().contains(DefaultRules.class)){
					rulesets.put(DefaultRules.class, new DefaultRules(this, p));
				}
			}else{
				if(!p.rulesets.containsKey(i)){
					p.log.warning("Can't find ruleset "+i+" in "+template.name+" config. Skipping.");
					continue;
				}
				try {
					Class<? extends UHCRuleset> r = p.getRuleset(i);
					if(r!=null && !rulesets.keySet().contains(r)){
						UHCRuleset ruleset = r.getConstructor(UHCGame.class, UHCPlugin.class).newInstance(this, p);
						rulesets.put(r, ruleset);
					}
				} catch (Throwable e) {
					if(e instanceof InvocationTargetException){
						e = ((InvocationTargetException)e).getCause();
					}
					p.logError("Error in ruleset instantiation.",e);
				}
			}
		}
		
	}
	
	public void startCountdown(int delay) {
		p.broadcast("Countdown initiated! Game will start in "+String.valueOf(delay)+" seconds.");
		countdownTaskId  = Bukkit.getScheduler().scheduleSyncRepeatingTask(p, new StartGameTask(delay, this), 20, 20);
		started = true;
	}

	public void stopCountdown() {
		if(countdownTaskId != -1){
			Bukkit.getScheduler().cancelTask(countdownTaskId);
			countdownTaskId = -1;
		}
	}
	
	private void stopTimer() {
		if(timerId != -1){
			Bukkit.getScheduler().cancelTask(timerId);
		}
	}
	
	public void startGame(){
		running = true;
		for(UHCRuleset i : rulesets.values()){
			i.onStart();
		}
		timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(p, new UHCTimerTask(p, this), 600, 600);
	}
	
	public void endGame(Player winningPlayer){
		stopTimer();
		stopCountdown();
		if(winningPlayer != null){
			UHCGameOverEvent ev = new UHCGameOverEvent(this, winningPlayer);
			Bukkit.getPluginManager().callEvent(ev);
			if(!ev.isCancelled()){
				running = false;
				Bukkit.getScheduler().cancelTask(timerId);
				p.broadcast(ev.getWinningPlayer().getDisplayName()+" has won!");
				UHCPostGameEvent ev_pg = new UHCPostGameEvent(this, winningPlayer);
				Bukkit.getPluginManager().callEvent(ev_pg);
				for(UHCRuleset i : rulesets.values()){
					i.onUnload();
				}
				p.gameOver();return;
			}
		} else{
			running = false;
			p.broadcast("The UHC game has ended.");
			for(UHCRuleset i : rulesets.values()){
				i.onUnload();
			}
			p.gameOver();return;
		}
	}
	

	public boolean removePlayer(OfflinePlayer player, boolean silent,
			boolean checkGame) {
		if (!players.contains(player)){return false;}
		UHCPlayerRemoveEvent ev = new UHCPlayerRemoveEvent(this, player, silent);
		Bukkit.getPluginManager().callEvent(ev);
		if(!ev.isCancelled()){
			OfflinePlayer opl = ev.getPlayer();
			players.remove(opl);
			Player pl = opl.getPlayer();
			if((opl.isOnline()) && !ev.isSilent()){
				pl.sendMessage(ChatColor.GOLD+"You've been removed from the game");
			}
			if(!checkGame){return true;}
			int onlinePlayers = getOnlinePlayers();
			if(onlinePlayers == 1){
				endGame(lastOnlinePlayer());
			} else if(onlinePlayers < 1){
				endGame(null);
			}
			return true;
		}
		return false;		
	}
	
	
	public boolean removePlayer(OfflinePlayer player, boolean silent) {
		return removePlayer(player, silent, true);
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent evp){
		if(!running){return;}
		Player pl = evp.getEntity();
		if(!players.contains(pl)){return;}
		UHCPlayerLoseEvent ev = new UHCPlayerLoseEvent(this, evp); 
		Bukkit.getPluginManager().callEvent(ev);
		if(!ev.isCancelled()){
			players.remove(ev.getEntity());
		}
		ev.overwriteDeathEvent(evp);
		
		int onlinePlayers = getOnlinePlayers();
		if(onlinePlayers == 1){
			endGame(lastOnlinePlayer());
		} else if(onlinePlayers < 1){
			endGame(null);
		}
	}

	private Player lastOnlinePlayer() {
		Player r = null;
		for(OfflinePlayer i : players){
			if(i.isOnline()){
				r = i.getPlayer();
			}
		}
		return r;
	}

	public int getOnlinePlayers() {
		int n = 0;
		for(OfflinePlayer i : players){
			if(i.isOnline()){
				n++;
			}
		}
		return n;
	}

	public String add(Player player) {
		UHCPlayerAddEvent ev = new UHCPlayerAddEvent(this, player);
		Bukkit.getPluginManager().callEvent(ev);
		if(!ev.isCancelled()){
			players.add(player); return null;	
		}
		return ev.getReason()!=null?ev.getReason():"";
		
	}

	
}
