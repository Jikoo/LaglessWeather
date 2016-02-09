package com.github.Jikoo.LaglessWeather;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LaglessWeather extends JavaPlugin implements Listener {

	private final int MINIMUM_DELAY = 20;

	private static LaglessWeather plugin;

	private Functions f;
	private int delay;
	protected Set<String> wasRaining;
	protected boolean enabledByDefault;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		if (new UpdateConfig().update()) {
			getLogger().warning("Your config.yml has been updated, please check it!");
		}
		f = Functions.getInstance();
		wasRaining = new HashSet<String>();
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("laglessweather").setExecutor(new CmdExecutor());
		f.updateGlobalWeather();
		delay = getConfig().getInt("ticks-per-time-update");
		if (delay < MINIMUM_DELAY) {
			delay = MINIMUM_DELAY;
			getConfig().set("ticks-per-time-update", MINIMUM_DELAY);
			saveConfig();
			getLogger().info("For safety reasons, set ticks-per-time-update to the minimum of " + MINIMUM_DELAY + ".");
		}
		enabledByDefault = this.getConfig().getBoolean("default-on");
		getLogger().info("Enabled LaglessWeather " + this.getDescription().getVersion());
	}
	
	@Override
	public void onDisable() {
		org.bukkit.event.HandlerList.unregisterAll((JavaPlugin) this);
		Bukkit.getServer().getScheduler().cancelTasks(this);
		f.save();
		f = null;
		wasRaining = null;
		plugin = null;
		getLogger().info("Disabled LaglessWeather " + this.getDescription().getVersion());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		triggerLoginCheck(event.getPlayer());
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (!event.isCancelled()) {
			if (event.toWeatherState()) {
				wasRaining.add(event.getWorld().getName());
				for (Player p : event.getWorld().getPlayers()) {
					if (f.isIncluded(p.getName()) && p.getPlayerWeather() == null) {
						p.setPlayerWeather(WeatherType.CLEAR);
						p.sendMessage(ChatColor.DARK_BLUE + "It begins to rain...");
					}
				}
			} else {
				if (wasRaining.remove(event.getWorld().getName())) {
					for (Player p : event.getWorld().getPlayers()) {
						if (f.isIncluded(p.getName()) && p.getPlayerWeather() != null) {
							p.sendMessage(ChatColor.GOLD + "The rain has stopped!");
							p.resetPlayerWeather();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (event.getPlayer().getWorld().hasStorm()) {
			if (f.isIncluded(event.getPlayer().getName())) {
				event.getPlayer().setPlayerWeather(WeatherType.CLEAR);
				event.getPlayer().sendMessage(ChatColor.DARK_BLUE + "It begins to rain...");
			}
		}
	}
	
	public void triggerLoginCheck(Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new LoginWeatherCheck(p));
	}
	
	public class LoginWeatherCheck implements Runnable {
		Player p;

		LoginWeatherCheck(Player player) {
			p = player;
		}

		@Override
		public void run() {
			if (p.getWorld().hasStorm() && f.isIncluded(p.getName())) {
				p.setPlayerWeather(WeatherType.CLEAR);
				p.sendMessage(ChatColor.DARK_BLUE + "It begins to rain...");
			}
		}
	}

	public static LaglessWeather getInstance() {
		return plugin;
	}
}
