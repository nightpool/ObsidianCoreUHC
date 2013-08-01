package net.nightpool.bukkit.nightutils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandRegister extends ClassRegister<CommandHandler> implements CommandExecutor {
	CommandPlugin p;
	
	public CommandRegister(CommandPlugin p) {
		super(p);
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command called, String arg1,
			String[] args) {
		if(args.length>0){
			List<String> flags = new ArrayList<String>();
			List<String> pos = new ArrayList<String>();
			List<String> all = new ArrayList<String>();
			for(int i=1;i<args.length;i++){
				if(args[i].startsWith("-")){
					flags.add(args[i]);
					all.add(args[i]);
				} else {
					pos.add(args[i]);
					all.add(args[i]);
				}
			}
			NCommand command = new NCommand(arg1, flags, pos, all, sender);
			
			Class<? extends CommandHandler> x = get(args[0]);
			if(x!=null){
				try {
					CommandHandler ch = instantiate(args[0], command, this.p);
					ch.call();
				} catch (BadArgException e){
					sender.sendMessage(ChatColor.RED+e.getMessage());
				} catch (InvocationTargetException e){
					//throw e.getCause();
					//throw e.getTargetException();
					
					p.getLogger().severe("Caught ite.");
				} catch (Throwable e) {
					p.logError("Error in command. ("+e.getMessage()+")", e);
					if(sender instanceof Player){
						sender.sendMessage(ChatColor.RED+"Error occurred in command. Check server logs for details.");
					}
				} 
				return true;
			} else {
				return printUsage(sender);
			}
		}
		return printUsage(sender);
	}

	public boolean printUsage(CommandSender sender){
		sender.sendMessage("Accepted commands are: ");
		String commands ="";
		for(String i : map.keySet()){
			commands+=i+" ";
		}
		sender.sendMessage(commands);
		return true;
	}
	
	public String printUsage(){
		String[] ca = getAll().keySet().toArray(new String[0]);
		String commands ="Accepted commands are: "+ca[0];
		for(int i=1; i<ca.length; i++){
			commands+=", "+ca[i];
		}
		return commands;
	}
}
