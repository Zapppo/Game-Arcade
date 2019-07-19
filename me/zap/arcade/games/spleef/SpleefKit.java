package me.zap.arcade.games.spleef;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zap.arcade.ArcadeManager;
import me.zap.arcade.kit.Kit;

/*
 * Here is an example of a game kit
 */
public class SpleefKit extends Kit {

	public SpleefKit(ArcadeManager arcadeManager) {
		super("Spleefer", null, new String[]{"Default Spleef Kit"}, arcadeManager);
	}

	@Override
	public void equip(Player player) {
		player.getInventory().setItem(0, new ItemStack(Material.IRON_SHOVEL));
	}
}
