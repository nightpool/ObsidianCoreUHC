package net.nightpool.bukkit.uhcplugin.commands;

import net.nightpool.bukkit.nightutils.CommandRegister;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;

public class UHCCommandRegister extends CommandRegister {
	UHCPlugin p;
	
	public UHCCommandRegister(UHCPlugin p) {
		super(p);
		this.p = p;
		this.register(UHCHelpCommand.class);
		this.register(TemplatesCommand.class);
		
		this.register(LoadGameCommand.class);
		this.register(StartGameCommand.class);
		this.register(StopGameCommand.class);
		this.register(RemovePlayerCommand.class);
		this.register(AddPlayerCommand.class);
		this.register(ListCommand.class);
		
		if(UHCPlugin.debug){
			this.register(ForceBorderChangeCommand.class);
		}
	}
}
