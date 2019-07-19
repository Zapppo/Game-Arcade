package me.zap.arcade.perk;

import org.bukkit.Material;

import me.zap.arcade.kit.Kit;

public abstract class KitPerk<KitType extends Kit> {
	private KitType kit;
	private String name;
	private String[] about;
	private Material material;

	public KitPerk(String name, KitType kit, String[] about, Material material) {
		this.name = name;
		this.kit = kit;
		this.about = about;
		this.material = material;
	}

	public abstract void activate();

	public Material getMaterial() {
		return material;
	}

	public String[] getInfo() {
		return about;
	}

	public String getName() {
		return name;
	}

	public KitType getType() {
		return kit;
	}
}
