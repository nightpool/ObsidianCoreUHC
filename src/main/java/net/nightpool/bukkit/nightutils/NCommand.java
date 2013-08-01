package net.nightpool.bukkit.nightutils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class NCommand {
	
	public NCommand(String command, List<String> flags, List<String> positional, List<String> all, CommandSender sender){
		this.command = command;
		this.flags=flags;
		this.positional=positional;
		this.all=all;
		this.sender=sender;
	}

	public final String command;
	public final List<String> flags;
	public final List<String> positional;
	public final List<String> all;
	public final CommandSender sender;
}
