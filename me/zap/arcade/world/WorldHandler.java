package me.zap.arcade.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import me.zap.arcade.game.GameMap;
import me.zap.arcade.game.MapToken;
import me.zap.arcade.game.event.GameEndEvent;
import me.zap.arcade.game.event.GameMapLoadEvent;
import me.zap.arcade.game.team.GameTeam;
import me.zap.arcade.game.team.TeamToken;
import me.zap.arcade.util.EmptyChunkGenerator;
import me.zap.arcade.util.FileUtil;
import me.zap.arcade.util.Module;
import me.zap.arcade.util.LocationUtil;

public class WorldHandler extends Module {
	private final List<GameMap> maps = new ArrayList<>();
	private UUID uuid;
	private World world;

	public WorldHandler(JavaPlugin plugin) {
		super(plugin);

		initializeMaps();
	}

	private void initializeMaps() {
		File file = new File("maps/");

		for (File cur : file.listFiles()) {
			if (!cur.isDirectory())
				continue;

			if (cur.getName().equalsIgnoreCase("spleef")) {
				for (File mapFolder : cur.listFiles()) {

					if (!mapFolder.isDirectory())
						continue;

					for (File content : mapFolder.listFiles())
						if (content.getName().equalsIgnoreCase("map.json"))
							convertStrJSON(content);
				}
			}
		}
	}

	private void convertStrJSON(File file) {
		String out = FileUtil.returnContents(file);
		MapToken token = new Gson().fromJson(out, MapToken.class);

		List<GameTeam> teams = new ArrayList<>();
		Location spawn = null;

		for (TeamToken team : token.teams) {
			GameTeam res = new GameTeam(team.name, team.maxSize, ChatColor.valueOf(team.color),
					LocationUtil.convertStr(team.spawn));
			teams.add(res);
		}

		Location location = LocationUtil.convertStr(token.spawn);
		if (location != null)
			spawn = location;

		GameMap map = new GameMap(token.name, teams, token.creators, spawn, file.getParentFile());
		maps.add(map);

		System.out.println(map.getName() + "> Added to maps");
	}

	@EventHandler
	public void worldUnload(GameEndEvent event) {
		Bukkit.unloadWorld("active/" + uuid, false);
	}

	@EventHandler
	public void worldChange(GameMapLoadEvent event) {
		this.uuid = UUID.randomUUID();

		try {
			FileUtils.copyDirectory(event.getMap().getFile(), new File("active/" + uuid));
		} catch (Exception e) {
			e.printStackTrace();
		}

		WorldCreator creator = new WorldCreator("active/" + uuid);
		creator.generator(new EmptyChunkGenerator());
		World world = creator.createWorld();
		world.setAutoSave(false);

		this.world = world;
	}

	public List<GameMap> getGameMaps() {
		return maps;
	}

	public World getWorld() {
		return world;
	}
}
