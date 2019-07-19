package me.zap.arcade.util;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemStackUtils {
	@SuppressWarnings("deprecation")
	public static ItemStack createStack(Material material, String name, byte data, int amount, String[] lore) {
		ItemStack stack = new ItemStack(material, amount, data);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		if (lore != null) 
		{
			meta.setLore(Arrays.asList(lore));
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createStack(Material material, String name, byte data, int amount) {
		ItemStack stack = new ItemStack(material, amount, data);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static void setLeatherColor(ItemStack stack, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
		meta.setColor(color);
		stack.setItemMeta(meta);
	}
	
	@SuppressWarnings("deprecation")
	public static void setUnbreakable(ItemStack stack, boolean bool)
	{
		ItemMeta meta = stack.getItemMeta();
		meta.spigot().setUnbreakable(bool);
		stack.setItemMeta(meta);
	}
	
	public static void enchant(ItemStack stack, Enchantment ench, int level)
	{
		stack.addUnsafeEnchantment(ench, level);
	}
	
	@SuppressWarnings("deprecation")
	public static void reset(Player player, boolean heal)
	{
		player.setAllowFlight(false);
		player.setFlying(false);
		
		player.setExp(0.0F);
		player.setFireTicks(0);
		player.setMaxHealth(20.0D);
		
		if (heal) {
			player.setHealth(20.0D);
			player.setFoodLevel(20);
		}
		
		player.setGameMode(GameMode.ADVENTURE);
		player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();
	}
}
