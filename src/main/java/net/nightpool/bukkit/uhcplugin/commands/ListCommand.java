package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.game.SpectatorRules;
import net.nightpool.bukkit.uhcplugin.game.UHCGame;

@Registerable(name="list", aliases = {"l"}, description="List players in current UHC game." +
		"\n(Will also list spectators if spectators are enabled)", usage="")
public class ListCommand extends UHCCommandHandler {

	public ListCommand(NCommand com, UHCPlugin p) {super(com, p);}

	@Override
	public void call() throws Exception {
		if(!checkGame()){return;}
		
		sender.sendMessage(ChatColor.GREEN+"Players: ");
		UHCGame game = p.getGame();
		for(OfflinePlayer i : game.players){
			p.log.info(i+"");
			if(i.isOnline()){
				sender.sendMessage(ChatColor.AQUA+"  "+i.getPlayer().getDisplayName());
			} else{
				sender.sendMessage(ChatColor.GOLD+"  "+i.getName()+" (offline)");
			}
		}
		if(game.rulesets.containsKey(SpectatorRules.class)){
			SpectatorRules s_rules = (SpectatorRules) game.rulesets.get(SpectatorRules.class);
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN+"Spectators: ");
			for(Player i : s_rules.spectators){
				sender.sendMessage(ChatColor.YELLOW+"  "+i.getDisplayName());
			}
			
		}
	}

}
