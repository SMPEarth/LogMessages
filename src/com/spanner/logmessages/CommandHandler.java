package com.spanner.logmessages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	LogMessages plugin = LogMessages.getPlugin(LogMessages.class);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("joinmessage")) {
			// handle
			return true;
		}
		if (command.getName().equals("leavemessage")) {
			// handle
			return true;
		}
		return false;
	}
	
}
