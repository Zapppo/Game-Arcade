package me.zap.arcade;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.zap.arcade.world.WorldHandler;

public class Arcade extends JavaPlugin {
	private ArcadeManager engine;
	
	@Override
	public void onEnable() {
		for (World world : getServer().getWorlds()) 
			for (Entity ent : world.getEntities())
				ent.remove();
		
		this.engine = new ArcadeManager(this, new WorldHandler(this));
	}
	
	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers())
			player.kickPlayer("Server restarting, please rejoin.");
		
		engine.disable();
	}
}
