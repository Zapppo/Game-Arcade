package me.zap.arcade.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.zap.arcade.ArcadeManager;
import me.zap.arcade.game.event.GameEndEvent;
import me.zap.arcade.game.team.GameTeam;
import me.zap.arcade.kit.Kit;
import me.zap.arcade.util.Module;
import me.zap.arcade.util.UtilPlayer;

public abstract class Game extends Module {
	private String name;
	private String[] about;

	public List<String> alive;
	public List<String> dead;

	private int required;

	private Map<String, RewardData> rewardData = new HashMap<>();

	private Kit[] kits;
	private ArcadeManager manager;

	public Game(String name, ArcadeManager manager, JavaPlugin plugin, String[] about, int required, Kit[] kits) {
		super(plugin);
		this.alive = new ArrayList<>();
		this.dead = new ArrayList<>();
		this.name = name;
		this.about = about;
		this.manager = manager;
		this.kits = kits;
		this.required = required;
	}

	protected Collection<? extends Player> getPlayers() {
		return Bukkit.getOnlinePlayers();
	}

	public List<String> getAlive() {
		return alive;
	}

	public List<String> getDead() {
		return dead;
	}

	public List<GameTeam> getTeams() {
		return getManager().getWorldManager().getGameMaps().get(getManager().getIndex()).getTeams();
	}

	public int getRequired() {
		return required;
	}

	protected ArcadeManager getManager() {
		return manager;
	}

	public boolean isDead(Player player) {
		if (getDead().contains(player.getName()))
			return true;
		return false;
	}

	public boolean isAlive(Player player) {
		if (getAlive().contains(player.getName()))
			return true;
		return false;
	}

	protected GameTeam getTeamFromPlayer(Player player) {
		for (GameTeam team : getTeams())
			if (team.getIn().contains(player.getName()))
				return team;
		return null;
	}

	public Kit[] getKits() {
		return kits;
	}

	public String getName() {
		return name;
	}

	public RewardData getRewardData(Player player) {
		return rewardData.get(player.getName());
	}

	protected List<Player> getAlivePlayers() {
		List<Player> players = new ArrayList<>();

		for (String cur : getAlive())
			players.add(Bukkit.getPlayer(cur));
		return players;
	}

	protected void messageAll(String... text) {
		for (Player player : getPlayers())
			UtilPlayer.message(player, text);
	}

	protected void sendTitleAll(String title, String subtitle) {
		for (Player player : getPlayers())
			UtilPlayer.sendTitle(player, title, subtitle);
	}

	public void end() {
		disable();

		getAlive().clear();
		getDead().clear();

		getPlugin().getServer().getPluginManager().callEvent(new GameEndEvent(this));
	}

	protected List<Player> getDeadPlayers() {
		List<Player> players = new ArrayList<>();

		for (String cur : getDead())
			players.add(Bukkit.getPlayer(cur));
		return players;
	}

	public String[] getAbout() {
		return about;
	}

	public Map<String, RewardData> getRewardDataMap() {
		return rewardData;
	}

	public abstract void start();
}	
