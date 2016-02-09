package com.github.Jikoo.LaglessWeather;

import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Functions {

	private HashSet<String> players;

	private static Functions instance;

	public static Functions getInstance() {
		if (instance == null) {
			instance = new Functions();
		}
		return instance;
	}

	public Functions() {
		// Why bother checking to see if a List contains a name when I can use a Set?
		// Woo laziness!
		players = new HashSet<String>(LaglessWeather.getInstance().getConfig().getStringList("players"));
	}

	public void addWeatherExempt(Player p) {
		if (LaglessWeather.getInstance().enabledByDefault) {
			players.remove(p.getName());
		} else {
			players.add(p.getName());
		}
		if (p.getWorld().hasStorm()) {
			p.sendMessage(ChatColor.DARK_BLUE + "It begins to rain...");
			p.setPlayerWeather(WeatherType.CLEAR);
		}
	}

	public void rmWeatherExempt(Player p) {
		if (LaglessWeather.getInstance().enabledByDefault) {
			players.add(p.getName());
		} else {
			players.remove(p.getName());
		}
		p.resetPlayerWeather();;
	}

	public boolean isIncluded(String name) {
		if (LaglessWeather.getInstance().enabledByDefault) {
			return !players.contains(name);
		}
		return players.contains(name);
	}

	public void save() {
		// While it's a little stupid to convert into a List just to save, it's easier on my brain.
		// Quite frankly, fixing up an abandoned project from a year(ish?) ago was enough hassle.
		// Inb4 someone actually made a similar plugin while I sat on this idea.
		LaglessWeather.getInstance().getConfig().set("players", new ArrayList<String>(players));
		LaglessWeather.getInstance().saveConfig();
	}

	/**
	 * Check and update weather globally
	 * (ensure weather is set on enable)
	 */
	public void updateGlobalWeather() {
		for (World w : Bukkit.getWorlds()) {
			if (w.hasStorm()) {
				LaglessWeather.getInstance().wasRaining.add(w.getName());
				for (Player p : w.getPlayers()) {
					if (this.isIncluded(p.getName())) {
						if (p.getPlayerWeather() == null) {
							p.setPlayerWeather(WeatherType.CLEAR);
							p.sendMessage(ChatColor.DARK_BLUE + "It begins to rain...");
						}
					} else {
						p.resetPlayerWeather();
					}
				}
			}
		}
	}
}
