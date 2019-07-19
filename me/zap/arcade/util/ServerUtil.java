package me.zap.arcade.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerUtil {
	public static Collection<? extends Player> getPlayers() {
		return Bukkit.getOnlinePlayers();
	}
}
