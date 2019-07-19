package me.zap.arcade.util;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Module implements Listener {
	protected JavaPlugin plugin;

	public Module(JavaPlugin plugin) {
		this.plugin = plugin;
		enable();
	}

	protected JavaPlugin getPlugin() {
		return plugin;
	}

	public void disable() {
		HandlerList.unregisterAll(this);
	}

	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
