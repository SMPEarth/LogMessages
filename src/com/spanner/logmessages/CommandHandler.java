package com.spanner.logmessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

public class CommandHandler implements CommandExecutor {
	LogMessages plugin = LogMessages.getPlugin(LogMessages.class);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) return false;
		
		if (command.getName().equals("logmessage")) {
			if (args[0].equalsIgnoreCase("givemessage")) {
				if (!sender.hasPermission("logmessages.give")) { sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command."); return true; }
				if (args.length != 3) {
					sender.sendMessage("Usage: /logmessage givemessage <username> <message type>");
					return true;
				}
				
				OfflinePlayer mentioned = Bukkit.getOfflinePlayer(args[1]); // Ought to find a better way of doing this, probably.
				if (mentioned == null) {
					sender.sendMessage(ChatColor.RED + "This player could not be found, they may have never logged in.");
					return true;
				}
				File playerdataFile = new File(plugin.getDataFolder() + "/playerdata/" + mentioned.getUniqueId());
				if (!playerdataFile.exists()) {
					JsonObject json = new JsonObject();
					JsonArray ownedarray = new JsonArray();
					ownedarray.add(args[2]);
					json.add("owned", ownedarray);
					try {
						FileUtils.writeStringToFile(playerdataFile, json.toString(), "UTF-8");
						sender.sendMessage("§2Added §a"+ args[2] +"§2 to user §a"+mentioned.getName());
						return true;
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "An error occurred. Check the console for a stack trace.");
						return true;
					}
				} else {
					try {
						List<String> lines = Files.readAllLines(playerdataFile.toPath());
						String fileString = String.join("", lines);
						JsonObject json = (JsonObject) plugin.parser.parse(String.join("", fileString));
						JsonArray ownedarray = (JsonArray) json.get("owned");
						if (ownedarray == null) {
							ownedarray = new JsonArray();
							ownedarray.add(args[2]);
							json.add("owned",ownedarray);
						} else {
							ownedarray.add(args[2]);
							json.add("owned",ownedarray);
						}
						FileUtils.write(playerdataFile, json.toString(), "UTF-8");
						sender.sendMessage("§2Added §a"+ args[2] +"§2 to user §a"+mentioned.getName());
						return true;
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "An error occurred. Check the console for a stack trace.");
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
