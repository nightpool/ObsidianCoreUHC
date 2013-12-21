package net.nightpool.bukkit.uhcplugin.game;

import java.util.Date;

import net.nightpool.bukkit.uhcplugin.UHCPlugin;

public class UHCTimerTask implements Runnable {

    final static double min_ms = 60*1000;
    
    private UHCGame game;
    private UHCPlugin p;
    private Date lastTime;
    private int msInterval;
    private int time;

    
    public UHCTimerTask(UHCPlugin p, UHCGame game) {
        this.p = p;
        this.game = game;
        this.lastTime = new Date();
        this.msInterval = (int) (this.game.template.timerIntervals * min_ms);
//        p.log.info("interval: " +msInterval);
        time = 0;
    }

    @Override
    public void run() {
        if(!game.running){
            return;
        }
        Date current = new Date();
        long diff = (current.getTime() - lastTime.getTime());
        //p.log.info("diff: "+diff);
        if (diff+100 >= msInterval){  //within .1 seconds is fine.
            time += Math.round(diff/min_ms);
            p.broadcast(String.valueOf(time)+" minutes elapsed!");
            lastTime = current;
        }
    }
}
