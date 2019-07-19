package me.zap.arcade.util;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class UtilPlayer {
	public static void message(Player player, String... strings) {
		player.sendMessage(strings);
	}

	@SuppressWarnings("deprecation")
	public static void sendTitle(Player player, String title, String subtitle) {
		player.sendTitle(title, subtitle);
	}

	public static void sendActionBar(Player player, String text)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
	}

	@SuppressWarnings("deprecation")
	public static void hidePlayer(Player player) {
		for (Player cur : ServerUtil.getPlayers())
			cur.hidePlayer(player);
	}

	@SuppressWarnings("deprecation")
	public static void showPlayer(Player player) {
		for (Player cur : ServerUtil.getPlayers())
			cur.showPlayer(player);
	}
}
