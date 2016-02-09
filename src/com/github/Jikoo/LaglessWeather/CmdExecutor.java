package com.github.Jikoo.LaglessWeather;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdExecutor implements CommandExecutor {
	
	Functions f = Functions.getInstance();

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (!(s instanceof Player)) {
			s.sendMessage("Toggled your weather. Really.");
			return true;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("on")) {
				f.addWeatherExempt((Player) s);
				s.sendMessage(ChatColor.DARK_GREEN + "Added you to weather exempt players.");
				return true;
			} else if (args[0].equalsIgnoreCase("off")) {
				f.rmWeatherExempt((Player) s);
				s.sendMessage(ChatColor.DARK_GREEN + "Removed you from weather exempt players.");
				return true;
			}
			return false;
		}
		return false;
	}

}
