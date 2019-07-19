package me.zap.arcade.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
	public static void playSound(Player player, Sound sound)
	{
		player.playSound(player.getLocation(), sound, 1F, 1F);
	}
}
