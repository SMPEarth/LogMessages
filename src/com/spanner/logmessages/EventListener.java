package com.spanner.logmessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EventListener implements Listener {
	LogMessages plugin = LogMessages.getPlugin(LogMessages.class);
	
	private String getCurrentLogMessage(Player p) {
		File playerdataFile = new File(plugin.getDataFolder() + "/playerdata/" + p.getUniqueId());
		if (!playerdataFile.exists()) return null;
		List<String> lines;
		try {
			lines = Files.readAllLines(playerdataFile.toPath());
		} catch (IOException err) {
			err.printStackTrace();
			return null;
		}
		String fileString = String.join("", lines);
		JsonObject json = (JsonObject) plugin.parser.parse(fileString);
		JsonElement current = json.get("current");
		if (current == null || current.isJsonNull()) return null;
		return current.getAsString();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String logMessage = getCurrentLogMessage(p);
		if (logMessage == null) {
			if (plugin.config.getBoolean("use-default")) {
				e.setJoinMessage(plugin.messages.getString(plugin.config.getString("default-message")+".join").replace("&", "§").replace("{username}", p.getName()));
				return;
			}
		}
		String joinMessage = plugin.messages.getString((logMessage+ ".join"));
		if (joinMessage == null) return;
		e.setJoinMessage(joinMessage.replace("&", "§").replace("{username}", p.getName()));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String logMessage = getCurrentLogMessage(p);
		if (logMessage == null) {
			if (plugin.config.getBoolean("use-default")) {
				e.setQuitMessage(plugin.messages.getString(plugin.config.getString("default-message")+".leave").replace("&", "§").replace("{username}", p.getName()));
				return;
			}
		}
		String joinMessage = plugin.messages.getString((logMessage+ ".leave"));
		if (joinMessage == null) return;
		e.setQuitMessage(joinMessage.replace("&", "§").replace("{username}", p.getName()));
	}
}
