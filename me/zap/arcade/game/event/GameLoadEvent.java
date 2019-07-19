package me.zap.arcade.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zap.arcade.game.Game;

public class GameLoadEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	private Game game;

	public GameLoadEvent(Game game) {
		this.game = game;
	}

	public Game GetGame() {
		return game;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
}
