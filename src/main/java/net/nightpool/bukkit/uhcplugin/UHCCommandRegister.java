package net.nightpool.bukkit.uhcplugin;

import net.nightpool.bukkit.nightutils.CommandRegister;
import net.nightpool.bukkit.uhcplugin.commands.*;

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
		
		this.register(ForceBorderChangeCommand.class);
	}
}
