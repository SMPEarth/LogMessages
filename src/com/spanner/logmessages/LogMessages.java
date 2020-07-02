package com.spanner.logmessages;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LogMessages extends JavaPlugin {

	FileConfiguration config;
	Logger logger;
	
	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		config = getConfig();
		logger = getLogger();
		
		getCommand("joinmessage").setExecutor(new CommandHandler());
		getCommand("leavemessage").setExecutor(new CommandHandler());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
}
