package net.nightpool.bukkit.uhcplugin.commands;

import net.nightpool.bukkit.nightutils.HelpCommand;
import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;

@Registerable(name="help", aliases = {"h"}, description="Gives help for a command")
public class UHCHelpCommand extends HelpCommand {

    public UHCHelpCommand(NCommand com, UHCPlugin p) {    
        super(com, p);
    }
}
