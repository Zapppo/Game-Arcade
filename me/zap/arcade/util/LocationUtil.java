package me.zap.arcade.util;

import org.bukkit.Location;

public class LocationUtil {
	public static Location convertStr(String str)
	{
		String[] axis = str.split(", ");
		
		double x = Double.parseDouble(axis[0]);
		double y = Double.parseDouble(axis[1]);
		double z = Double.parseDouble(axis[2]);		
		Location loc = new Location(null, x, y, z);
		
		if (axis.length == 5)
		{
			loc.setYaw(Float.parseFloat(axis[3]));
			loc.setPitch(Float.parseFloat(axis[4]));
		}
		return loc;
	}
}
