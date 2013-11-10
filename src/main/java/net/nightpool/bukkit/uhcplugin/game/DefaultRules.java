package net.nightpool.bukkit.uhcplugin.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.nightpool.bukkit.uhcplugin.UHCPlugin;
import net.nightpool.bukkit.uhcplugin.config.SubConfig;
import net.nightpool.bukkit.uhcplugin.game.UHCRuleset.RulesetConfig;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerAddEvent;
import net.nightpool.bukkit.uhcplugin.events.UHCPlayerLoseEvent;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.wimbli.WorldBorder.BorderData;

@RulesetConfig(config = DefaultRules.DefaultRulesConfig.class, key = "default")
public class DefaultRules extends UHCRuleset implements Listener{

	public BorderData currentBorder;
	private Map<Integer, Integer> playerBorders;
	private List<Recipe> oldRecipes;
	
	public DefaultRules(UHCGame game, UHCPlugin p) {
		super(game, p);
		
		registerRecipes();
		createBoundaries();
		Bukkit.getPluginManager().registerEvents(this, p);
	}
	
	@Override
	public void onStart(){
		initPlayers();
		game.world.setTime(0);
	}
	
	@Override
	public void onUnload(){
		HandlerList.unregisterAll(this);
		undoBoundaries();
		unregisterRecipes();
	}

	private void createBoundaries() {
		Location spawn = game.world.getSpawnLocation();
		BorderData border = new BorderData(spawn.getX(), spawn.getZ(), getConfig().getInt("starting-boundaries"), getConfig().getInt("starting-boundaries"), false, false);
		com.wimbli.WorldBorder.PluginlessConfig.setKnockBack(.5);
		com.wimbli.WorldBorder.PluginlessConfig.setBorder(game.world.getName(), border);
		com.wimbli.WorldBorder.PluginlessConfig.setMessage("You seem to have wandered outside of the boundaries!");
		com.wimbli.WorldBorder.PluginlessConfig.setWhooshEffect(true);
		com.wimbli.WorldBorder.PluginlessConfig.load(p,false);
		this.currentBorder = border;
		
		playerBorders = new HashMap<Integer, Integer>();
		for(Entry<Double, Integer> i : getConfig().getDubIntMap("auto-bounds").entrySet()){
			if(Math.floor(i.getKey()) == i.getKey()){
				playerBorders.put(i.getKey().intValue(), i.getValue());
			}else if(i.getKey() < 1 && i.getKey() > 0){	
				int pl_val = (int)Math.round(i.getKey()*game.players.size());
				if(playerBorders.containsKey(pl_val)){
					continue;
				}
				playerBorders.put(pl_val, i.getValue());
			}
		}
	}

	private void undoBoundaries() {
		com.wimbli.WorldBorder.PluginlessConfig.removeBorder(game.world.getName());
	}

	private void initPlayers() {
		for(OfflinePlayer i : game.players){
			prep_player(i);
		}
		if(getConfig().getBool("use-gamerules") && game.world.isGameRule("naturalRegeneration")){
			game.world.setGameRuleValue("naturalRegeneration", "false");
		}
		game.world.setDifficulty(Difficulty.HARD);
		
		Scoreboard m = Bukkit.getScoreboardManager().getMainScoreboard();
		Objective h = m.getObjective("health");
		if(h != null){
			h.unregister();
		}
		m.clearSlot(DisplaySlot.PLAYER_LIST);
		Objective o = m.registerNewObjective("health", "health");
		o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for(OfflinePlayer i : game.players){
			if(i.isOnline()){
				o.getScore(i).setScore((int) Math.round((i.getPlayer().getMaxHealth()*2)));
			}
		}
	}

	private void prep_player(OfflinePlayer i) {
		Player pl = i.getPlayer();
		if(pl==null){return;}
		pl.setHealth(pl.getMaxHealth());
		pl.setFoodLevel(20);
		pl.getInventory().clear();
		pl.setGameMode(GameMode.SURVIVAL);
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void playerAddEvent(UHCPlayerAddEvent e){
		prep_player(e.getPlayer());
	}

	private void registerRecipes() {
		Iterator<Recipe> ri =  Bukkit.recipeIterator();
		oldRecipes = new ArrayList<Recipe>();
		List<Recipe> recipies = new ArrayList<Recipe>();
		recipies.add(new GoldAppleRecipe());
		recipies.add(new GoldCarrotRecipe());
		recipies.add(new GlisteringMelonRecipe());
		if(getConfig().getBool("disable-enchanted-apple")){
			recipies.add(new DummyEnchantedAppleRecipe());
		}
		while(ri.hasNext()){
			Recipe n = ri.next();
			for(Recipe r : recipies){
				if(n.getResult().equals(r.getResult())){
					oldRecipes.add(n);
					ri.remove();
				}
			}
		}
		for(Recipe r : recipies){
			Bukkit.addRecipe(r);
		}
	}
	
	private void unregisterRecipes() {
		Iterator<Recipe> ri = Bukkit.recipeIterator();
		while(ri.hasNext()){
			Recipe n = ri.next();
			for(Recipe r : oldRecipes){
				if(n.getResult().equals(r)){
					ri.remove();
				}
			}
		}
		for(Recipe r : oldRecipes){
			Bukkit.addRecipe(r);
		}
	}

	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void handleTearsDrops(EntityDeathEvent e){
		if(!e.getEntityType().equals(EntityType.GHAST)){return;}
		if(!game.running){return;}
		
		List<ItemStack> drops = e.getDrops();
		List<ItemStack> remove = new ArrayList<ItemStack>();
		List<ItemStack> add = new ArrayList<ItemStack>();
		for(ItemStack i : drops){
			if(i.isSimilar(new ItemStack(Material.GHAST_TEAR))){
				remove.add(i);
				add.add(new ItemStack(Material.GOLD_INGOT, i.getAmount()));
			}
		}
		for(ItemStack i : remove){
			drops.remove(i);
		}
		for(ItemStack i : add){
			drops.add(i);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void handlePlayerLose(UHCPlayerLoseEvent ev){
		int n_players = game.players.size()-1;
		if(playerBorders.containsKey(n_players)){
			Bukkit.getScheduler().runTask(p, new NewBorderTask(playerBorders.get(n_players), currentBorder, game));
		}
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void handleRegen(EntityRegainHealthEvent e){
		if(game.running && !getConfig().getBool("use-gamerules")){
			Entity en = e.getEntity();
			if(en instanceof Player){
				Player p = (Player) en;
				if(game.players.contains(p)){
					if(e.getRegainReason().equals(RegainReason.SATIATED)){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	

	public static class DefaultRulesConfig extends SubConfig {
		static final Map<Double, Integer> default_autobounds_map = new HashMap<Double, Integer>();
		static{
			default_autobounds_map.put(.5, 1000);
			default_autobounds_map.put(2.0, 200);
		}
		
		public DefaultRulesConfig(ConfigurationSection fromSection) {
			addInt("starting-boundaries", 2000);
			addInt("border-time", 5);
			addBool("use-gamerules", false);
			addBool("vote-bounds", true);
			addBool("disable-enchanted-apple", false);
			addDubIntMap("auto-bounds", default_autobounds_map);
			fromConfig(fromSection);
		}
	}
	
	public static class GoldAppleRecipe extends ShapedRecipe {
		
		public GoldAppleRecipe() {
			super(new ItemStack(Material.GOLDEN_APPLE));
			this.shape("AAA","ABA","AAA");
			this.setIngredient('A', Material.GOLD_INGOT);
			this.setIngredient('B', Material.APPLE);
		}
		
	}
	public static class DummyEnchantedAppleRecipe extends ShapedRecipe{
		public static ItemStack ENCHANTED_APPLE = new ItemStack(Material.GOLDEN_APPLE);
		static{
			ENCHANTED_APPLE.setData(new MaterialData(Material.GOLDEN_APPLE, (byte) 1));
		}
		
		
		public DummyEnchantedAppleRecipe(){
			super(ENCHANTED_APPLE);
			this.shape("AAA","ABA","AAA");
			this.setIngredient('A', Material.FIRE);
			this.setIngredient('B', Material.REDSTONE_TORCH_ON);
		}
	}
	public static class GoldCarrotRecipe extends ShapedRecipe {

		public GoldCarrotRecipe() {
			super(new ItemStack(Material.GOLDEN_CARROT));
			this.shape("AAA","ABA","AAA");
			this.setIngredient('A', Material.GOLD_INGOT);
			this.setIngredient('B', Material.CARROT_ITEM);
		}
		
	}
	public static class GlisteringMelonRecipe extends ShapelessRecipe {

		public GlisteringMelonRecipe() {
			super(new ItemStack(Material.SPECKLED_MELON));
			this.addIngredient(Material.MELON);
			this.addIngredient(Material.GOLD_BLOCK);
		}
		
	}
}
