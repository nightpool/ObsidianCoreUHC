package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;

@Registerable(name="add", aliases = {"a"}, description="Add a player to the current UHC game.", usage="")
public class AddPlayerCommand extends UHCCommandHandler {

	public AddPlayerCommand(NCommand com, UHCPlugin p) {
		super(com, p);
	}

	@Override
	public void call() throws Exception {
		if(!checkCanAdminAndGame()){
			return;
		}
		if(pos.size()<1){
			sender.sendMessage(ChatColor.RED+"You need to include the name of the player");return;
		}
		String name = pos.get(0);
		Player pl = Bukkit.getPlayer(name);
		if (p==null){
			sender.sendMessage(ChatColor.RED+name+" is not valid player!");
		}
		String reason = p.getGame().add(pl);
		if(reason == null){
			sender.sendMessage(ChatColor.GREEN+"Player "+pl.getDisplayName()+" has been added successfully!");
		} else{
			sender.sendMessage(ChatColor.RED+"Adding player "+pl.getDisplayName()+" failed. "+reason);
		}
	}

}
