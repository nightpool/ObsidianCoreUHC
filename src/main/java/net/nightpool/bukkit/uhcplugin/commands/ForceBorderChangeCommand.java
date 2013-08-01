package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.game.DefaultRules;
import net.nightpool.bukkit.uhcplugin.game.NewBorderTask;
import net.nightpool.bukkit.uhcplugin.game.UHCGame;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset;

@Registerable(name="forceborder", aliases = {}, description="Force a change in border", usage="new-radius")
public class ForceBorderChangeCommand extends UHCCommandHandler {

	public ForceBorderChangeCommand(NCommand com, UHCPlugin p) {
		super(com, p);
	}

	@Override
	public void call() throws Exception {
		if(pos.size()<1){
			sender.sendMessage(ChatColor.RED+"Need size"); return;
		}
		if(!this.sender.hasPermission("uhc.admin")){
			sender.sendMessage("Insufficient permissions.");
		}
		int size;
		try{
			size = Integer.valueOf(pos.get(0));
		} catch(NumberFormatException e){
			sender.sendMessage(ChatColor.RED+pos.get(0)+" is not a valid int"); return;
		}
		DefaultRules d = null;
		UHCGame game = p.getGame();
		for(UHCRuleset i : game.rulesets){
			if(i instanceof DefaultRules){
				d = (DefaultRules) i;
			}
		}
		if(d == null){
			sender.sendMessage(ChatColor.RED+"Couldn't find DefaultRules. Are you sure you're running a game with ruleset Default?"); return;
		}
		
		Bukkit.getScheduler().runTask(p, new NewBorderTask(size, d.currentBorder, game));

	}

}
