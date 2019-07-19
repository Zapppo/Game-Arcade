package me.zap.arcade.game.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameTeam {
	private List<String> players = new ArrayList<>();
	private Location spawn;
	private ChatColor color;
	private String name;
	private int max;

	public GameTeam(String name, int max, ChatColor color, Location spawn) {
		this.max = max;
		this.name = name;
		this.color = color;
		this.spawn = spawn;
	}

	public int getMax() {
		return max;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void addTeamPlayer(Player player) {
		players.add(player.getName());
	}

	public List<String> getIn() {
		return players;
	}

	public void delTeamPlayer(Player player) {
		players.remove(player.getName());
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	public Color getArmorColor() {
		if (color == ChatColor.BLUE)
			return Color.BLUE;
		if (color == ChatColor.RED)
			return Color.RED;
		if (color == ChatColor.GREEN)
			return Color.GREEN;
		if (color == ChatColor.YELLOW)
			return Color.YELLOW;
		return Color.WHITE;
	}
}
