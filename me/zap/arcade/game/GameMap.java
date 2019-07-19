package me.zap.arcade.game;

import java.io.File;
import java.util.List;

import org.bukkit.Location;

import me.zap.arcade.game.team.GameTeam;

public class GameMap {
	private List<GameTeam> teams;
	private String name;
	private String[] authors;
	private Location spawn;
	private File file;

	public GameMap(String name, List<GameTeam> teams, String[] authors, Location spawn, File file) {
		this.spawn = spawn;
		this.name = name;
		this.teams = teams;
		this.authors = authors;
		this.file = file;
	}

	public List<GameTeam> getTeams() {
		return this.teams;
	}

	public File getFile() {
		return file;
	}

	public Location getSpawn() {
		return spawn;
	}

	public String getName() {
		return name;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public String[] getCreators() {
		return authors;
	}
}
