package net.nightpool.bukkit.uhcplugin.commands;


import org.bukkit.ChatColor;

import net.nightpool.bukkit.nightutils.NArrayUtils;
import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.UHCTemplate;

@Registerable(name="templates", aliases = {"t"}, description="List the current templates, or get info about a template.")
public class TemplatesCommand extends UHCCommandHandler {
    
    
    public TemplatesCommand(NCommand com, UHCPlugin p) {
        super(com, p);
        
    }

    @Override
    public void call() throws Exception {
        if(this.pos.size() > 0){
            UHCTemplate t = p.getTemplates().get(this.pos.get(0));
            sender.sendMessage(ChatColor.GREEN + "Template " + t.name + " details:");
            sender.sendMessage(ChatColor.GREEN + "  timer-intervals:  " + ChatColor.DARK_RED + t.timerIntervals);
            
            sender.sendMessage(ChatColor.GREEN + "  approve-players:  " +ChatColor.AQUA + t.manualPlayers);
            
            sender.sendMessage(ChatColor.GREEN + "  rulesets:  "+ ChatColor.GRAY + NArrayUtils.toList(t.rulesets.toArray(new String[0])));
        }else{
            String[] x = {};
            x = p.getTemplates().keySet().toArray(x);
            sender.sendMessage(ChatColor.GREEN + "Templates: "+NArrayUtils.toListColor(x, ChatColor.AQUA, ChatColor.GRAY));
        }
    }
//
//    private String[] stMapToStA(Map<Double, Integer> autoBounds) {
//        List<String> output = new ArrayList<String>();
//        for(Entry<Double, Integer> e : autoBounds.entrySet()){
//            output.add(e.getKey() + ": "+ e.getValue());
//        }
//        return output.toArray(new String[0]);
//    }
    
}
