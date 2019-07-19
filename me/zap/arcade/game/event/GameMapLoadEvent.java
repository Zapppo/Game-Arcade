package me.zap.arcade.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zap.arcade.game.Game;
import me.zap.arcade.game.GameMap;

public class GameMapLoadEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	private Game game;
	private GameMap map;

	public GameMapLoadEvent(Game game, GameMap map) {
		this.map = map;
		this.game = game;
	}

	public GameMap getMap() {
		return map;
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
