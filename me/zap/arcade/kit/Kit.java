package me.zap.arcade.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zap.arcade.ArcadeManager;
import me.zap.arcade.game.Game;

public abstract class Kit {
	private Game game;
	private String name;
	private String[] lore;
	private ItemStack[] contents;
	private ArcadeManager arcadeManager;

	public Kit(String name, ItemStack[] contents, String[] lore, ArcadeManager arcadeManager) {
		this.arcadeManager = arcadeManager;
		this.name = name;
		this.lore = lore;
		this.contents = contents;
	}

	protected ArcadeManager getManager() {
		return this.arcadeManager;
	}

	public String getName() {
		return name;
	}

	public String[] getLore() {
		return this.lore;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public Game getGame() {
		return this.game;
	}

	public abstract void equip(Player player);
}
