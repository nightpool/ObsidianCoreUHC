package net.nightpool.bukkit.nightutils;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class CommandHandler {
	protected List<String> flags;
	protected List<String> pos;
	protected List<String> all;
	protected CommandSender sender;
	protected CommandPlugin p;
	protected String commandName;
	
	//Screw this. With these, I can just hardcode the names in CommandRegister.
	
	public CommandHandler(NCommand com, CommandPlugin p){
		this.flags=com.flags;
		this.pos=com.positional;
		this.all=com.all;
		this.sender = com.sender;
		this.commandName =com.command;
		this.p = p;
	}
	
	public abstract void call() throws Exception;
}