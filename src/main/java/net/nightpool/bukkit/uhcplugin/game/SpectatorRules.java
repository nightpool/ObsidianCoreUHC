package net.nightpool.bukkit.uhcplugin.game;

import java.util.HashSet;
import java.util.Set;
  
import net.nightpool.bukkit.nightutils.NCommand;
import net.nightpool.bukkit.nightutils.Registerable;
import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.commands.UHCCommandHandler;
import net.nightpool.bukkit.uhcplugin.config.SubConfig;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerAddEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerLoseEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@UHCRuleset.RulesetConfig(config = SpectatorRules.SpectatorRulesConfig.class, key = "spectator")
public class SpectatorRules extends UHCRuleset implements Listener {

    public Set<Player> spectators;
    /* 
     * TODO things that don't work
     * chat commands (/say, /me)
     * pushing entities
     * 
    */
    public SpectatorRules(UHCGame game, UHCPlugin p) {
        super(game, p);
        Bukkit.getPluginManager().registerEvents(this, p);
        this.spectators = new HashSet<Player>();
        if(!p.getCommandRegister().getAll().containsValue(SpectatorTeleportCommand.class)){
            p.getCommandRegister().register(SpectatorTeleportCommand.class);
        }
    }

    @Override
    public void onStart() {
        for(Player i : game.world.getPlayers()){
            if(!game.players.contains(i)){
                makeSpec(i);
            }
        }
        
    }

    private void makeSpec(Player spec) {
        spectators.add(spec);
        spec.setGameMode(GameMode.CREATIVE);
        spec.getInventory().clear();
        spec.leaveVehicle();
        for(OfflinePlayer i : game.players){
            if(i.isOnline()){
                Player i_p = i.getPlayer();
                i_p.hidePlayer(spec);
            }
        }
        for(Player i : spectators){
            spec.showPlayer(i);
        }
    }

    @Override
    public void onUnload() {
        for(OfflinePlayer i : game.players){
            if(i.isOnline()){
                Player i_p = i.getPlayer();
                for(Player j : spectators){
                    i_p.showPlayer(j);
                }
            }
        }
        HandlerList.unregisterAll(this);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent e){
        if(!game.running){return;}
        Player player = e.getPlayer();
        if(!player.hasPermission("uhc.spectate") && !getConfig().getBool("can-join")){
            player.kickPlayer("Spectators are not allowed without the correct permissions!");
            e.setJoinMessage("");
        }
        if(game.players.contains(Bukkit.getOfflinePlayer(player.getName()))){
            for(Player i : spectators){
                player.hidePlayer(i);
            }
        }else{
            player.sendMessage(ChatColor.YELLOW+"You have joined a game in progress and are now a spectator.");
            e.setJoinMessage("");
            makeSpec(player);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void playerLeaveEvent(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(spectators.contains(player)){
            spectators.remove(player);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void playerRemoveEvent(UHCPlayerRemoveEvent e){
        if(e.getPlayer().isOnline()){
            Player player = e.getPlayer().getPlayer();
            makeSpec(player);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void playerLoseEvent(UHCPlayerLoseEvent e){
        final Player player = e.player;
        Bukkit.getScheduler().runTask(p, new Runnable(){
            @Override
            public void run(){
                makeSpec(player);
            }
        });
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void playerAddEvent(UHCPlayerAddEvent e){
        OfflinePlayer opl = e.getPlayer();
        if(!opl.isOnline()){return;}
        Player player = opl.getPlayer();
        if(spectators.contains(player)){
            spectators.remove(player);
        }
        for(Player i : spectators){
            player.hidePlayer(i);
        }
    }
    
    public void onPlayerEvent(PlayerEvent ev) {
        onPlayerEvent(ev, ev.getPlayer());
    }
    public void onEntityEvent(EntityEvent ev) {
        onEntityEvent(ev, ev.getEntity());
    }
    
    public void onPlayerEvent(PlayerEvent ev, Player pl){
        if(!game.running){return;}
        if(!spectators.contains(ev.getPlayer())){return;}
        if(ev instanceof Cancellable){
            ((Cancellable)ev).setCancelled(true);
        } else {
            p.getLog().info("Bad event "+ev.getEventName()+" for "+ev.getPlayer()+" but couldn't cancel.");
        }
    }
    
    void logPlayerEvent(PlayerEvent ev){
        p.getLog().info("Canceled "+ev.getEventName()+" for "+ev.getPlayer());
    }
    
    public void onEntityEvent(EntityEvent ev, Entity e){
        if(!game.running){return;}
        if(!spectators.contains(e)){return;}
        if(ev instanceof Cancellable){
            ((Cancellable)ev).setCancelled(true);
        } else {
            p.getLog().info("Bad event "+ev.getEventName()+" for "+e+" but couldn't cancel.");
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleBlockPlace(BlockPlaceEvent e){
        if(!game.running){return;}
        if(!spectators.contains(e.getPlayer())){
            return;
        }
        e.setCancelled(true);
        return;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleDeathEvent(PlayerDeathEvent e){
        if(!game.running || !spectators.contains(e.getEntity())){
            return;
        }
        e.setDeathMessage("");
        e.setDroppedExp(0);
        e.getDrops().clear();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerBedEnterEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerBucketFillEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerBucketEmptyEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerDropItemEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerEditBookEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerFishEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerInteractEntityEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = false) // needs to be this way because of interact w/ water brokenness.
    public void handlePlayerEvent(PlayerInteractEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerItemBreakEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerShearEntityEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerEvent(PlayerPickupItemEvent e){onPlayerEvent(e);}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleEntityEvent(EntityDamageByEntityEvent e){onEntityEvent(e, e.getDamager());}
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleEntityEvent(EntityTargetEvent e){if(e.getTarget() != null){onEntityEvent(e, e.getTarget());}}

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void chatEvent(AsyncPlayerChatEvent e){
        if(spectators.contains(e.getPlayer())){
            e.getRecipients().removeAll(game.players);
            e.setFormat(getConfig().getString("chat-prefix")+" "+ChatColor.RESET+e.getFormat());
        }
    }
    
    public static class SpectatorRulesConfig extends SubConfig {
        public SpectatorRulesConfig(ConfigurationSection fromSection){
            addBool("can-join", true);
            addString("chat-prefix", ChatColor.GOLD+"[SPECTATOR]");
            fromConfig(fromSection);
        }
    }
    

    @Registerable(name="teleport", aliases = {"tp"}, description="Allows spectators to teleport around to different players", usage="player")
    public static class SpectatorTeleportCommand extends UHCCommandHandler{

        public SpectatorTeleportCommand(NCommand com, UHCPlugin p) {
            super(com, p);
        }

        @Override
        public void call() throws Exception {
            if (!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED+"This command can only be used by players!"); return;
            }
            if(!p.gameRunning() )
            if(pos.size() < 1){
                sender.sendMessage(ChatColor.RED+"You need to specify a player!"); return;
            }
            String name = pos.get(0);
            Player player = Bukkit.getPlayer(name);
            if(player == null){
                sender.sendMessage(ChatColor.RED+"Can't find player "+name); return;
            }
            sender.sendMessage(ChatColor.GREEN+"Teleporting to "+name);
            ((Player) sender).teleport(player, TeleportCause.COMMAND);
        }
        
    }
}
