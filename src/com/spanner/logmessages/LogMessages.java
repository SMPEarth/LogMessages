package com.spanner.logmessages;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonParser;

public class LogMessages extends JavaPlugin {

	FileConfiguration config;
	FileConfiguration messages;
	JsonParser parser = new JsonParser();
	Logger logger;
	
	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveResource("messages.yml", false);
		saveConfig();
		
		config = getConfig();
		messages = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "messages.yml"));
		logger = getLogger();
		
		getCommand("logmessage").setExecutor(new CommandHandler());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
}
