package me.zap.arcade.game;

public class RewardData {
	public int coins;
	public double xp;

	public int getCoins() {
		return coins;
	}

	public double getXP() {
		return xp;
	}

	public void delete() {
		this.coins = 0;
		this.xp = 0.0D;
	}
}
