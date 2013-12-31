package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.UHCTemplate;

@Registerable(name="start", aliases = {"st"}, description="Start the loaded UHCGame.", usage="[delay]")
public class StartGameCommand extends UHCCommandHandler {

    public StartGameCommand(NCommand com, UHCPlugin p) {super(com, p);}

    @Override
    public void call() throws Exception {
        if(!checkCanAdminAndGame(true)){
            if(checkRunPerms()){
                World world = (sender instanceof Player) ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);
                UHCTemplate t = p.getTemplates().get("default");
                if(t == null){
                    sender.sendMessage(ChatColor.RED+"No game loaded and no default template to load.");return;
                }
                sender.sendMessage(ChatColor.YELLOW+"No game loaded. Loading one with \"default\" template on world \""+world.getName()+"\".");
                p.loadGame(t, sender, world);
                p.broadcast("New game loaded! (Template: "+t.name+") "+
                        (p.getGame().canStart()? ChatColor.GREEN+"Ready to play!" : ChatColor.YELLOW+"Setup in progress"));
                if(!p.getGame().canStart()){
                    return;
                }
            }
        }
        if(p.getGame().running || p.getGame().started){
            sender.sendMessage(ChatColor.RED+"There's already a game running!"); return;
        }
        if(!p.getGame().canStart()){
            sender.sendMessage(ChatColor.RED+"The game isn't ready to start yet");
        }
        int delay;
        if(pos.size()<1){
            delay = 20;
        } else{
            try{
                delay = Integer.valueOf(pos.get(0));
            } catch (NumberFormatException e){
                delay = 20; // TODO: add to template config.
            }
        }
        
        boolean success = p.getGame().startCountdown(delay);
        if(success){
            sender.sendMessage(ChatColor.GREEN+"Game started sucessfully!");
        } else{
            sender.sendMessage(ChatColor.RED+"Game isn't ready to start yet");
        }
    }    

}
