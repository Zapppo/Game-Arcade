package me.zap.arcade.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zap.arcade.game.Game;

public class GameEndEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	private Game game;

	public GameEndEvent(Game game) {
		this.game = game;
	}

	public Game getGame() {
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
