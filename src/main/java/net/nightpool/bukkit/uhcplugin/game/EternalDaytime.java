package net.nightpool.bukkit.uhcplugin.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.SubConfig;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;

@RulesetConfig(config = EternalDaytime.EternalDaytimeConfig.class, key = "eternal-daytime")
public class EternalDaytime extends UHCRuleset {
    
    DaytimeTask task;
    
    public EternalDaytime(UHCGame game, UHCPlugin p) {
        super(game, p);
    }

    @Override
    public void onStart() {
        task = new DaytimeTask();
        int i = getConfig().getInt("interval");
        task.runTaskTimer(p, i, i);
    }

    @Override
    public void onUnload() {
        try{
            task.cancel();
        } catch (IllegalStateException e){}
    }
    
    public class DaytimeTask extends BukkitRunnable {
        long startTime;    
        public DaytimeTask() {
            startTime = game.world.getFullTime();
        }

        @Override
        public void run() {
            game.world.setFullTime(startTime);
        }
    }
    
    public static class EternalDaytimeConfig extends SubConfig {
        public EternalDaytimeConfig(ConfigurationSection fromConfig){
            addInt("interval", 20);
        }
    }

}
