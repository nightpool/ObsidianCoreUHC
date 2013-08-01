package net.nightpool.bukkit.uhcplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.nightpool.bukkit.nightutils.CommandHandler;
import net.nightpool.bukkit.nightutils.NCommand;

public abstract class UHCCommandHandler extends CommandHandler {
	protected UHCPlugin p;
	
	public UHCCommandHandler(NCommand com, UHCPlugin p) {
		super(com, p);
		this.p = p;
	}
	

	protected boolean checkGame(boolean suppress_message) {
		if (!p.gameRunning()){
			if(!suppress_message){
				sender.sendMessage(ChatColor.RED+"This command only makes sense if a game is already running!");
			}
			return false;
		}
		return true;
	}
	
	protected boolean checkCanAdminAndGame(boolean suppress_message){
		if(!checkGame(suppress_message)){return false;}
		boolean canAdmin = this.sender.hasPermission("uhc.admin") || 
				this.sender.equals(Bukkit.getConsoleSender()) || 
				this.sender.equals(p.getGame().runner);
		if (canAdmin){
			return true;
		}else{
			if(!suppress_message){
				sender.sendMessage(ChatColor.RED+"You need to be able to administrate the current game to preform this command.");
			}
			return false;
		}
	}


	protected boolean checkGame() {
		return checkGame(false);
	}

	protected boolean checkCanAdminAndGame(){
		return checkCanAdminAndGame(false);
	}
	
	protected boolean checkRunPerms(boolean suppress_message){
		boolean canRun = (sender.hasPermission("uhc.admin") || sender.hasPermission("uhc.run") || sender.equals(Bukkit.getConsoleSender()));
		if(!canRun && !suppress_message){
			sender.sendMessage(ChatColor.RED+"You don't have the permissions to preform this command.");
		}
		return canRun;
	}
	
	protected boolean checkRunPerms(){
		return checkRunPerms(false);
	}
}
