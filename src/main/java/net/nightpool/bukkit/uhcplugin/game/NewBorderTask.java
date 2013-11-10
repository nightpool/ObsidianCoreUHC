package net.nightpool.bukkit.uhcplugin.game;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.wimbli.WorldBorder.BorderData;

public class NewBorderTask implements Runnable {

	public enum BorderReason {
		TIME, PLAYERS

	}


	private int newRadius;
	private BorderData border;
	private UHCGame game;
	private BorderData newBorder;
	private int borderTime;
	private BukkitTask enforceTask;

	public NewBorderTask(int newRadius, BorderData border, UHCGame game) {
		this.newRadius = newRadius;
		this.border = border;
		this.newBorder = border.copy();
		newBorder.setRadius(newRadius);
		this.game = game;
		this.borderTime = game.template.getSubConfig(DefaultRules.class.getAnnotation(RulesetConfig.class).key()).getInt("border-time");
	}

	@Override
	public void run() {
		int n = checkPlayers();
		String plural = n == 1 ? "is "+n+" player" : "are "+n+" players";
		game.p.broadcast("The new border radius is "+newRadius+". There "+plural+" that "+(n==1?"is":"are")+" outside of this border. You have "+borderTime+" minutes before the border changes.");
		enforceTask = Bukkit.getScheduler().runTaskTimer(game.p, new EnforceBorderTask(this, borderTime), 20, 20);
	}
	
	public void setBorder(BorderReason reason){
		if(enforceTask != null){
			enforceTask.cancel();
		}
		if(reason == BorderReason.TIME){
			game.p.broadcast("Time has expired! New borders are coming under effect.");
		} else if (reason == BorderReason.PLAYERS){
			game.p.broadcast("All players are within the border! New borders are coming under effect.");
		} else{
			game.p.broadcast("New borders are coming under effect");
		}
		border.setRadius(newRadius);
	}
	
	public int checkPlayers(){
		return violatingPlayers().size();
	}
	
	public List<Player> violatingPlayers(){
		List<Player> li = new ArrayList<Player>();
		for (OfflinePlayer p : game.players){
			if(p.isOnline()){
				Location l = ((Player)p).getLocation();
				if(!newBorder.insideBorder(l.getX(), l.getZ(), false)){
					li.add((Player)p);
				}
			}
		}
		return li;
	}
	

	public class EnforceBorderTask implements Runnable {
		private Date startTime;
		private long borderTime;
		private NewBorderTask borderTask;
		public EnforceBorderTask(NewBorderTask borderTask, int borderTime) {
			startTime = new Date();
			this.borderTime = borderTime*60*1000;
			this.borderTask = borderTask;
		}

		@Override
		public void run() {
			Date now = new Date();
			long diff = now.getTime() - startTime.getTime();
			if(diff > borderTime){
				borderTask.setBorder(BorderReason.TIME);
				return;
			}
			
			if(borderTask.checkPlayers() == 0){
				borderTask.setBorder(BorderReason.PLAYERS);
				return;
			}
		}

	}

}
