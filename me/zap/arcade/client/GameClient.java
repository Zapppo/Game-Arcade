package me.zap.arcade.client;

import me.zap.arcade.ArcadeManager;
import me.zap.arcade.kit.Kit;

public class GameClient {
	private boolean playing;
	private Kit kit;

	public GameClient(ArcadeManager arcadeManager) {
		this.playing = false;
		this.kit = arcadeManager.getDefaultKit();
	}

	public Kit getKit() {
		return this.kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public void setPlaying(boolean bool) {
		this.playing = bool;
	}

	public boolean isPlaying() {
		return this.playing;
	}
}
