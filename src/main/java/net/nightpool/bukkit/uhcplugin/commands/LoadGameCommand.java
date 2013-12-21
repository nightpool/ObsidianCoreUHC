package net.nightpool.bukkit.uhcplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.UHCTemplate;

@Registerable(name="load", aliases = {"l"}, description="Load a new UHC game from a template." +
        "\n This sets the game up and gets you ready to start playing.", usage="template [world]")
public class LoadGameCommand extends UHCCommandHandler {

    public LoadGameCommand(NCommand com, UHCPlugin p) {super(com, p);}

    @Override
    public void call() throws Exception {
        if(checkGame(true)){
            sender.sendMessage(ChatColor.RED+"This command doesn't make sense if there's already a game running!");return;
        }
        if(!checkRunPerms()){return;}
        String template;
        if(pos.size()<1){
            template = "default";
            sender.sendMessage(ChatColor.YELLOW+"No template specified, using template \"default\".");
        } else{
            template = pos.get(0);
            
        }
        UHCTemplate t = p.getTemplates().get(template);
        if(t == null){
            sender.sendMessage(ChatColor.RED + template+" is not a valid template!");return;
        }
        World world;
        if(pos.size()<2){
            if(sender instanceof Player){
                world = ((Player) sender).getWorld();
            }else{
                world = Bukkit.getWorlds().get(0);
            }
            sender.sendMessage(ChatColor.YELLOW+"No world specified, using world \""+world.getName()+"\".");
        } else{
            world = Bukkit.getWorld(pos.get(1));
            if(world == null){
                sender.sendMessage(ChatColor.RED+pos.get(1)+" is not a valid world");return;
            }
        }
        p.loadGame(t, sender, world);
        sender.sendMessage(ChatColor.GREEN+"Game loaded successfully!");
        p.broadcast("New game loaded! (Template: "+t.name+")");
    }

}
