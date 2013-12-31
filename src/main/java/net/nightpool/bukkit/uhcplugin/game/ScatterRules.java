package net.nightpool.bukkit.uhcplugin.game;

import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.SubConfig;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@RulesetConfig(config = ScatterRules.ScatterRulesConfig.class, key = "scatter")
public class ScatterRules extends UHCRuleset implements Listener{

    public ScatterRules(UHCGame game, UHCPlugin p) {
        super(game, p);
    }

    @Override
    public void onStart(){
        if(!game.rulesets.containsKey(DefaultRules.class)){
            p.getLog().warning("Scatter ruleset enabled without Default ruleset. Doing nothing.");
            return;
        }
        DefaultRules default_rules = (DefaultRules) game.rulesets.get(DefaultRules.class);
        double mag = default_rules.currentBorder.getRadiusX()*0.5;
        double angle = Math.PI*2/game.players.size();
        double inc = angle;
        Location d;
        for(OfflinePlayer o : game.players){
            if(!o.isOnline())
                continue;
            Player p = o.getPlayer();
            d = default_rules.center.clone().add(Math.cos(angle)*mag, 0, Math.sin(angle)*mag);
            p.teleport(d.getWorld().getHighestBlockAt(d).getLocation(d));
            angle += inc;
        }
    }

    @Override
    public void onUnload() {}

    public static class ScatterRulesConfig extends SubConfig {
        public ScatterRulesConfig(ConfigurationSection fromSection) {
            fromConfig(fromSection);
        }
    }
}
