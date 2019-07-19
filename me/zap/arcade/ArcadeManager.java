package me.zap.arcade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.zap.arcade.client.GameClient;
import me.zap.arcade.game.GameCancellable;
import me.zap.arcade.game.Game;
import me.zap.arcade.game.GameMap;
import me.zap.arcade.game.RewardData;
import me.zap.arcade.game.event.GameEndEvent;
import me.zap.arcade.game.event.GameMapLoadEvent;
import me.zap.arcade.game.team.GameTeam;
import me.zap.arcade.games.spleef.SpleefGame;
import me.zap.arcade.kit.Kit;
import me.zap.arcade.util.ItemStackUtils;
import me.zap.arcade.util.Module;
import me.zap.arcade.util.SoundUtil;
import me.zap.arcade.util.UtilPlayer;
import me.zap.arcade.util.ServerUtil;
import me.zap.arcade.world.WorldHandler;

public class ArcadeManager extends Module {
	private WorldHandler worldManager;

	private Map<Entity, Kit> kitMap = new HashMap<>();
	private Map<String, GameClient> clientMap = new HashMap<>();

	private int index = 0;
	private int countdown = 10;

	private final Location spawn;
	private final List<Game> games = new ArrayList<>();
	private final List<Location> kitLocations = new ArrayList<>();
	private final String line = ChatColor.STRIKETHROUGH + "----------------------------------------";
	
	private boolean started = false;

	/*
	 * Initialize the game engine
	 */
	public ArcadeManager(JavaPlugin plugin, WorldHandler worldManager) {
		super(plugin);

		this.worldManager = worldManager;
		this.spawn = new Location(Bukkit.getWorld("world"), -219.5, 10, 129.5, -180, 0);
		
		games.add(new SpleefGame(this, plugin));
		
		for (Game game : games)
			game.disable();

		kitLocations.add(new Location(Bukkit.getWorld("world"), -219.5, 5.5, 110.5));

		loadGameLobby(games.get(index));
	}

	/*
	 * End the current game running
	 */
	public void endGame(Game game) {
		game.disable();
		
		for (Player player : ServerUtil.getPlayers()) {
			GameClient client = getPlayerFromGame(player);
			client.setPlaying(false);
			
			ItemStackUtils.reset(player, client.isPlaying());
			
			giveLobbyItems(player);
			
			player.teleport(getSpawn());
			player.setPlayerListName(ChatColor.GRAY + player.getName());
			
			UtilPlayer.showPlayer(player);
		}
		
		for (GameTeam team : game.getTeams())
			team.getIn().clear();
		
		index++;
		
		if (index >= games.size())
			this.index = 0;
	
		loadGameLobby(getGame());
		recieveRewards(game);
		
		game.getRewardDataMap().clear();
		
		this.started = false;
		
		if (hasRequiredPlayers(getGame()))
		{
			this.started = true;
			
			announceGame();
			startCountDown();
		}
	}

	/*
	 * Start the game currently selected
	 */
	private void startGame(Game game) {
		GameMap map = getWorldManager().getGameMaps().get(0);

		getPlugin().getServer().getPluginManager().callEvent(new GameMapLoadEvent(game, map));

		for (Player player : ServerUtil.getPlayers()) {
			ItemStackUtils.reset(player, true);

			getPlayerFromGame(player).setPlaying(true);

			GameTeam playerTeam = getSmallestTeam(game);
			playerTeam.addTeamPlayer(player);
			
			player.setPlayerListName(playerTeam.getColor() + player.getName());
			player.setGameMode(GameMode.SURVIVAL);

			sendGameInfo(player, game);

			game.alive.add(player.getName());
			game.getRewardDataMap().put(player.getName(), new RewardData());

			UtilPlayer.message(player, playerTeam.getColor() + "You are on " + ChatColor.BOLD + playerTeam.getName() + " Team");
		}

		game.start();
		game.enable();
	}

	/*
	 * Load all kit entities for the lobby as well as removing all old kit entities
	 */
	private void loadGameLobby(Game game) {
		for (Entity ent : kitMap.keySet())
			ent.remove();

		kitMap.clear();

		for (int i = 0; i < kitLocations.size(); i++) {
			Location kitLocation = kitLocations.get(i);
			Kit kit = game.getKits()[i];

			ArmorStand kitEntity = kitLocation.getWorld().spawn(kitLocation, ArmorStand.class);
			kitEntity.setArms(true);
			kitEntity.setCustomName(ChatColor.GREEN + kit.getName());
			kitEntity.setCustomNameVisible(true);

			if (kit.getContents() != null && kit.getContents().length == 3) {
				kitEntity.setHelmet(kit.getContents()[0]);
				kitEntity.setChestplate(kit.getContents()[1]);
				kitEntity.setLeggings(kit.getContents()[2]);
				kitEntity.setBoots(kit.getContents()[3]);
			}

			kitMap.put(kitEntity, kit);
		}
	}

	/*
	 * Disable the game engine and world manager This is called when the plugin is
	 * disabled
	 */
	public void disable() {
		for (Entity ent : kitMap.keySet())
			ent.remove();
		Bukkit.unloadWorld(getWorldManager().getWorld().getName(), false);
	}

	@EventHandler
	public void gameEnd(GameEndEvent event) {
		System.out.println("Ending game");
		endGame(event.getGame());
	}

	@EventHandler
	public void entityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) 
			return;
		
		if (!getPlayerFromGame((Player)event.getEntity()).isPlaying()) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void playerDamagePlayer(EntityDamageByEntityEvent event) {
		if (!started) 
			return;
	
		if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) 
			return;

		Player damager = (Player) event.getDamager();
		Player victim = (Player) event.getEntity();

		if (!(getPlayerFromGame(damager).isPlaying() && getPlayerFromGame(victim).isPlaying())) {
			event.setCancelled(true);
			return;
		}

		for (GameTeam team : getGame().getTeams()) {
			if (!(team.getIn().contains(damager.getName()) && team.getIn().contains(victim.getName()))) 
				continue;
				
			if (getGame() instanceof GameCancellable)
				event.setCancelled(!((GameCancellable)getGame()).playerDamageMate(damager, victim));
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if (!getPlayerFromGame(event.getPlayer()).isPlaying()) 
			event.setCancelled(true);
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		if (!getPlayerFromGame(event.getPlayer()).isPlaying())
			event.setCancelled(true);
	}

	@EventHandler
	public void dropItem(PlayerDropItemEvent event) {
		if (!getPlayerFromGame(event.getPlayer()).isPlaying())
			event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void pickupItem(PlayerPickupItemEvent event) {
		if (!getPlayerFromGame(event.getPlayer()).isPlaying()) 
			event.setCancelled(true);
	}

	@EventHandler
	public void weatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) 
			event.setCancelled(true);
	}

	@EventHandler
	public void thunderChange(ThunderChangeEvent event) {
		if (event.toThunderState()) 
			event.setCancelled(true);
	}

	@EventHandler
	public void foodChange(FoodLevelChangeEvent event) {
		if (!getPlayerFromGame((Player)event.getEntity()).isPlaying()) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}

	@EventHandler
	public void interactEntity(PlayerInteractAtEntityEvent event) {
		if (getPlayerFromGame(event.getPlayer()).isPlaying()) 
			return;
		
		event.setCancelled(true);

		Entity ent = event.getRightClicked();
		Player player = event.getPlayer();
		GameClient client = getPlayerFromGame(player);
		Kit kit = kitMap.get(ent);

		if (kit != null) {
			client.setKit(kit);

			SoundUtil.playSound(player, Sound.ENTITY_ITEM_BREAK);
			SoundUtil.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
			UtilPlayer.message(player, ChatColor.GREEN + "Selected " + kit.getName() + " as your kit");

			kit.equip(player);
		}
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		player.teleport(getSpawn());
		player.setPlayerListName(ChatColor.GRAY + player.getName());

		clientMap.put(player.getName(), new GameClient(this));

		ItemStackUtils.reset(player, true);
		
		giveLobbyItems(player);

		if (hasRequiredPlayers(getGame())) {
			if (started)
				return;
			this.started = true;

			announceGame();
			startCountDown();
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		clientMap.remove(event.getPlayer().getName());
	}

	protected void giveLobbyItems(Player player) {
		player.getInventory().setContents(null);
	}

	protected void recieveRewards(Game game) {
		for (Player player : ServerUtil.getPlayers()) {
			if (game.getRewardData(player) != null) {
				RewardData data = game.getRewardData(player);
				informRewards(player, data);
			} else {
				UtilPlayer.message(player,
					new String[] { "", ChatColor.DARK_GRAY + line, ChatColor.RED + "No rewards x(",
					ChatColor.RED + "You didn't play", ChatColor.DARK_GRAY + line});
			}
			SoundUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
		}
	}

	public GameTeam getSmallestTeam(Game game) {
		GameTeam smallest = null;
		
		for (GameTeam team : game.getTeams()) {
			if (smallest == null) {
				smallest = team;
				continue;
			}

			double teamSize = ((double) team.getIn().size()) / team.getMax();
			double smallestSize = ((double) smallest.getIn().size()) / smallest.getMax();
			
			if (teamSize < smallestSize) {
				smallest = team;
				continue;
			}

			if (teamSize == smallestSize && ThreadLocalRandom.current().nextBoolean()) 
				smallest = team;
		}
		return smallest;
	}

	protected void startCountDown() {
		new BukkitRunnable() {
			int count = countdown;

			@Override
			public void run() {
				if (!hasRequiredPlayers(getGame())) {
					this.cancel();
					notifyGameCancelled(getGame());

					started = false;
					return;
				}

				count--;

				if (count <= 0) {
					this.cancel();

					startGame(getGame());
				}
			}
		}.runTaskTimer(getPlugin(), 1L, 20L);
	}

	private void informRewards(Player player, RewardData data) {
		String[] msg = new String[] { "", ChatColor.DARK_GRAY + line, ChatColor.AQUA + "" + ChatColor.BOLD + "REWARDS",
				ChatColor.YELLOW + "+" + data.getCoins() + " coins", ChatColor.DARK_GREEN + "+" + data.getXP() + " xp",
				ChatColor.DARK_GRAY + line, };
		player.sendMessage(msg);
	}

	public Kit getDefaultKit() {
		return games.get(index).getKits()[0];
	}

	private Location getSpawn() {
		return spawn;
	}

	public GameClient getPlayerFromGame(Player player) {
		return clientMap.get(player.getName());
	}

	private Game getGame() {
		return games.get(index);
	}

	/*
	 * Notify players that the game count down has been cancelled
	 */
	private void notifyGameCancelled(Game game) {
		for (Player player : ServerUtil.getPlayers()) {
			SoundUtil.playSound(player, Sound.ENTITY_BLAZE_DEATH);

			UtilPlayer.message(player, ChatColor.RED + "" + ChatColor.BOLD + "Error" + ChatColor.RESET + ChatColor.GRAY
					+ " - " + ChatColor.RED + "Not enough players");
		}
	}

	/*
	 * Announce that the game is starting in seconds
	 */
	protected void announceGame() {
		for (Player player : ServerUtil.getPlayers()) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + getGame().getName() + ChatColor.RESET
					+ ChatColor.GRAY + " - " + ChatColor.GREEN + "Starting in " + countdown + " seconds!");

			SoundUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING);
		}
	}

	/*
	 * Give a player all properties for spectator
	 */
	public void applySpectator(Player player) {
		getPlayerFromGame(player).setPlaying(false);
		ItemStackUtils.reset(player, true);

		UtilPlayer.hidePlayer(player);
		player.setAllowFlight(true);
		player.teleport(getSpectatorSpawn(games.get(0)));
		player.setFlying(true);
		player.setPlayerListName(ChatColor.GRAY + player.getName());
		player.setGameMode(GameMode.SPECTATOR);

		UtilPlayer.sendTitle(player, ChatColor.GREEN + "SPECTATING", "Please wait until game ends");
	}

	public Location getSpectatorSpawn(Game game) {
		Location spawn = getWorldManager().getGameMaps().get(0).getSpawn();
		spawn.setWorld(getWorldManager().getWorld());
		return spawn;
	}

	private void sendGameInfo(Player player, Game game) {
		player.sendMessage(ChatColor.DARK_GRAY + line);
		player.sendMessage("");
		player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + game.getName().toUpperCase());
		player.sendMessage(game.getAbout());
		player.sendMessage("");
		player.sendMessage(ChatColor.DARK_GRAY + line);
	}

	protected boolean hasRequiredPlayers(Game game) {
		if (ServerUtil.getPlayers().size() >= game.getRequired())
			return true;
		return false;
	}

	public WorldHandler getWorldManager() {
		return worldManager;
	}

	public boolean hasStarted() {
		return started;
	}

	public int getIndex() {
		return index;
	}
}
