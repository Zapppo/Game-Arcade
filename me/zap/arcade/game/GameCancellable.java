package me.zap.arcade.game;

import org.bukkit.entity.Player;

public interface GameCancellable {
	boolean playerDamageMate(Player damager, Player victim);
}
