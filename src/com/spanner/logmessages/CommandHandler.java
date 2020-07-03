package com.spanner.logmessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

public class CommandHandler implements CommandExecutor {
	LogMessages plugin = LogMessages.getPlugin(LogMessages.class);

	private void errorMessage(Exception e, CommandSender sender) {
		e.printStackTrace();
		sender.sendMessage(ChatColor.RED + "An error occurred. Check the console for a stack trace.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0 && sender instanceof Player) {
			Player p = (Player)sender;
			File playerdataFile = new File(plugin.getDataFolder() + "/playerdata/" + p.getUniqueId());
			JsonArray ownedMessages = new JsonArray();
			if (playerdataFile.exists()) {
				List<String> lines;
				try {
					lines = Files.readAllLines(playerdataFile.toPath());
					String fileString = String.join("", lines);
					JsonObject json = (JsonObject) plugin.parser.parse(fileString);
					ownedMessages = (JsonArray) json.get("owned");
				} catch (IOException e) { errorMessage(e,sender); return true; }
			}
			if (ownedMessages.size() == 0) {
				p.sendMessage(ChatColor.GOLD + "You don't own any log messages!");
				return true;
			}
			List<String> ownedMessagesStrings = new ArrayList<String>();
			ownedMessages.forEach(logmessage -> {ownedMessagesStrings.add(logmessage.getAsString());});
			String responseMessage = "§6Your log messages: §f";
			responseMessage += String.join("§7,§f ", ownedMessagesStrings);
			p.sendMessage(responseMessage);
			return true;
		}
		
		if (command.getName().equals("logmessage")) {
			if (args[0].equalsIgnoreCase("givemessage") || args[0].equalsIgnoreCase("give")) {
				if (!sender.hasPermission("logmessages.give")) { sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command."); return true; }
				if (args.length != 3) {
					sender.sendMessage("Usage: /logmessage givemessage <username> <message type>");
					return true;
				}
				
				OfflinePlayer mentioned = Bukkit.getOfflinePlayer(args[1]); // Ought to find a better way of doing this, probably.
				if (!mentioned.hasPlayedBefore()) {
					sender.sendMessage(ChatColor.RED + "This player has never played before, or does not exist.");
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
					} catch (IOException e) { errorMessage(e,sender); return true; }
				} else {
					try {
						List<String> lines = Files.readAllLines(playerdataFile.toPath());
						String fileString = String.join("", lines);
						JsonObject json = (JsonObject) plugin.parser.parse(fileString);
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
					} catch (IOException e) { errorMessage(e,sender); return true; }
				}
			}
			
			if (!(sender instanceof Player)) { sender.sendMessage("You must be a player to use this command."); return true; }
			if (!sender.hasPermission("logmessages.switch")) { sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command."); return true; }
			Player p = (Player)sender;
			if (plugin.messages.getKeys(false).contains(args[0])) {
				File playerdataFile = new File(plugin.getDataFolder() + "/playerdata/" + p.getUniqueId());
				JsonArray ownedMessages = new JsonArray();
				if (playerdataFile.exists()) {
					List<String> lines;
					try {
						lines = Files.readAllLines(playerdataFile.toPath());
						String fileString = String.join("", lines);
						JsonObject json = (JsonObject) plugin.parser.parse(fileString);
						ownedMessages = (JsonArray) json.get("owned");
						for (JsonElement jsonelement : ownedMessages) {
							if (jsonelement.getAsString().equals(args[0])) {
								json.addProperty("current", args[0]);
								FileUtils.write(playerdataFile, json.toString(), "UTF-8");
								p.sendMessage(ChatColor.GREEN + "Updated log message!");
								return true;
							}
						}
					} catch (IOException e) { errorMessage(e,sender); return true; }
				}
			} else {
				sender.sendMessage(ChatColor.RED + "This log message does not exist. Ensure you have spelled and capitalized it correctly.");
				return true;
			}
			
		}
		return false;
	}
	
}
