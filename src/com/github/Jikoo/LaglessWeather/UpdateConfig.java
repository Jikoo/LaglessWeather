package com.github.Jikoo.LaglessWeather;

import java.util.Set;

public class UpdateConfig {

	LaglessWeather plugin = LaglessWeather.getInstance();
	
	public boolean update() {
		Set<String> options = plugin.getConfig().getDefaults().getKeys(false);
		Set<String> current = plugin.getConfig().getKeys(false);
		boolean changed = false;

		for (String s : options) {
			if (!current.contains(s)) {
				plugin.getConfig().set(s, plugin.getConfig().getDefaults().get(s));
				changed = true;
			}
		}

		if (changed) {
			plugin.saveConfig();
		}
		return changed;
	}
}