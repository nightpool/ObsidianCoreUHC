package net.nightpool.bukkit.uhcplugin.game;

import java.util.Collections;
import java.util.List;

import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.SubConfig;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;
import net.nightpool.bukkit.uhcplugin.utils.OfflinePlayerSet;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

@RulesetConfig(config = TeamRules.ScatterRulesConfig.class, key = "scatter")
public class TeamRules extends UHCRuleset implements Listener{
    
    public static class Team {
        public OfflinePlayerSet players = new OfflinePlayerSet();
        public String leader;
        public Team(OfflinePlayer pl){
            leader = pl.getName();
            players.add(pl);
        }
        public boolean isLeader(OfflinePlayer pl){
            return leader == pl.getName();
        }
        public OfflinePlayer getLeader(){
            return Bukkit.getPlayer(leader);
        }
    }

    public List<Team> teams;
    public BukkitTask task;

    public TeamRules(UHCGame game, UHCPlugin p) {
        super(game, p);
        game.canStart++;
        List<OfflinePlayer> playerOrder = Lists.newArrayList(game.players);
        Collections.shuffle(playerOrder);
        p.broadcast("Picking order: ");
        for(OfflinePlayer i : playerOrder){
            p.broadcast("   "+i.getName());
        }
        int n_teams = getConfig().getInt("amount");
        if(getConfig().getString("type").equals("size")){
            n_teams = playerOrder.size() / getConfig().getInt("size");
        }
        if(n_teams < 2){
            n_teams = 2;
        }
        teams = Lists.newArrayListWithCapacity(n_teams);
        if(playerOrder.size() <= n_teams){
            p.broadcast("Not enough players to support selection, assigning teams randomly.");
            for(OfflinePlayer i : playerOrder){
                teams.add(new Team(i));
            }
            announce_teams();
            return;
        }
        task = Bukkit.getScheduler().runTask(p, new TeamPickingTask(playerOrder, n_teams));
    }

    private void announce_teams() {
        p.broadcast("Final Teams: ");
        for(int i = 0; i<teams.size(); i++){
            p.broadcast("Team "+(i+1)+": "+StringUtils.join(teams.get(i).players.name_iterator(), ", "));
        }
    }

    @Override
    public void onStart(){
        task.cancel();
    }

    @Override
    public void onUnload() {
        task.cancel();
    }


    public class TeamPickingTask implements Runnable, Listener {
        private List<OfflinePlayer> playerOrder;
        private int n_teams;
        private int current_team = 0;
        private int players;
        
        private int round = 1;
        private OfflinePlayerSet picked = new OfflinePlayerSet();

        public TeamPickingTask(List<OfflinePlayer> playerOrder, int n_teams) {
            this.playerOrder = playerOrder;
            this.n_teams = n_teams;
            players = playerOrder.size();
            Bukkit.getPluginManager().registerEvents(this, p);
        }

        @Override
        public void run() {
            p.getLog().info("RUN: order: "+StringUtils.join(playerOrder, ", ")+
                    " n_teams: "+n_teams+
                    " current_team: "+current_team+
                    " round: "+round+
                    " picked: "+StringUtils.join(picked, ", "));
            if(picked.size() >= players){
                p.broadcast("Team selection completed!");
                p.broadcast("Teams are:");
                for(Team i : teams){
                    p.broadcast(StringUtils.join(i.players, ", "));
                }
                AsyncPlayerChatEvent.getHandlerList().unregister(this);
                return;
            }
            if(current_team >= n_teams){
                current_team = 0;
                round++;
            }
            OfflinePlayer picker;
            if(teams.get(current_team)!=null){
                if (playerOrder.size() < 1) {
                    p.broadcast("Not enough players online to complete team selection. Assigning teams randomly.");
                    teams.clear();
                    List<OfflinePlayer> players = Lists.newArrayList(game.players);
                    for (int i = 0; i < n_teams; i++) {
                        teams.add(new Team(players.remove(0)));
                    }
                    for (int i = 0; i < players.size(); i++) {
                        teams.get(i % n_teams).players.add(players.get(i));
                    }
                }
                OfflinePlayer o = playerOrder.remove(0);
                if(!o.isOnline()){
                    playerOrder.add(o);
                    p.broadcast(o.getName()+" is offline. Skipping.");
                    run(); return;
                }
                teams.add(current_team, new Team(o));
                teams.get(current_team).leader = o.getName();
                picked.add(o);
                picker = o;
            } else {
                picker = teams.get(current_team).getLeader();
            }
            p.broadcast("Team "+(current_team+1)+" leader "+picker.getName()+", pick your "+getOrdinal(round)+" team member.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onChat(AsyncPlayerChatEvent e){
            if(!e.getPlayer().getName().equals(playerOrder.get(current_team).getName()))
                return;
            final String message = e.getMessage();
//            final Player player = e.getPlayer();
            Bukkit.getScheduler().runTask(p, new Runnable(){
                @Override
                public void run() {
                    p.getLog().info("ONCHAT: order: "+StringUtils.join(playerOrder, ", ")+
                        " n_teams: "+n_teams+
                        " current_team: "+current_team+
                        " round: "+round+
                        " picked: "+StringUtils.join(picked, ", "));
                    OfflinePlayer n = Bukkit.getPlayer(message);
                    if(n == null){
                        n = Bukkit.getOfflinePlayer(message);
                        if(n == null){
                            return;
                        }
                    }
                    if(picked.contains(n)){
                        p.broadcast(message+" has already been picked. Try again."); return;
                    }
                    teams.get(current_team).players.add(n);
                    playerOrder.remove(n);
                    current_team++;
                    TeamPickingTask.this.run();
                }
            });
        }

    }
    
    
    /*
     * Credit to Bohemian on StackOverflow, where this code comes from
     * http://stackoverflow.com/a/6810409/584871
     */
    public static String getOrdinal(int num){
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (num % 100) {
            case 11:
            case 12:
            case 13:
                return num + "th";
            default:
                return num + sufixes[num % 10];
        }
    }
    
    public static class ScatterRulesConfig extends SubConfig {
        public ScatterRulesConfig(ConfigurationSection fromSection) {
            addString("type", "size"); // other option is amount.
            addInt("size", 2);
            addInt("amount", 4);
            addString("rounding", "over");
            fromConfig(fromSection);
        }
    }
}
