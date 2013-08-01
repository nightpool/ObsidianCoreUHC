package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;


@Registerable(name="remove", aliases = {"rm", "rem"}, description="Remove a player from the current UHC game." +
		"\n-s makes it silent", usage="player [-s]")
public class RemovePlayerCommand extends UHCCommandHandler {

	public RemovePlayerCommand(NCommand com, UHCPlugin p) {super(com, p);}

	@Override
	public void call() throws Exception {
		if (!checkCanAdminAndGame()){return;}
		if(this.pos.size() < 1){
			sender.sendMessage(ChatColor.RED + "This command requires at least one argument, the name of a player to remove.");return;
		} 
		String name = pos.get(0);
		OfflinePlayer pl = Bukkit.getPlayer(name);
		if(pl == null){
			pl = Bukkit.getOfflinePlayer(name);
			if(!pl.hasPlayedBefore()){
				sender.sendMessage(ChatColor.RED+"Player "+name+" not found.");
				return;
			}
		}
		p.getGame().removePlayer(pl, flags.contains("-s"));
	}
}
