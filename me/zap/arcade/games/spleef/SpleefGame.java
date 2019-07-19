package me.zap.arcade.games.spleef;

import org.bukkit.plugin.java.JavaPlugin;

import me.zap.arcade.ArcadeManager;
import me.zap.arcade.game.Game;
import me.zap.arcade.kit.Kit;

/*
 * Here is an example of a arcade game
 */
public class SpleefGame extends Game {

	public SpleefGame(ArcadeManager manager, JavaPlugin plugin) {
		super("Spleef Example", manager, plugin, new String[]{"Spleef your enemies!"}, 2, 
		new Kit[]{new SpleefKit(manager)});
	}

	@Override
	public void start() {}
}
