package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.ChatColor;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;

@Registerable(name="stop", aliases = {}, description="Stop the current UHCGame.", usage="")
public class StopGameCommand extends UHCCommandHandler {

    public StopGameCommand(NCommand com, UHCPlugin p) {super(com, p);}

    @Override
    public void call() throws Exception {
        if(!checkCanAdminAndGame()){
            return;
        }
        p.getGame().endGame((String)null);
        sender.sendMessage(ChatColor.GREEN+"Game stopped sucessfully!");
    }

}
