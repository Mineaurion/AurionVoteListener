package com.mineaurion.aurionvotelistener.commands;

import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.aurionvotelistener.Main;

public class ForcequeueCmd implements CommandExecutor {
	private Main plugin;

	public ForcequeueCmd(Main main) {
		plugin = main;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src.hasPermission("*") || src.hasPermission("listener.admin")) {

			List<String> player = plugin.switchsql.QueuePlayer();
			for (int i = 0; i < player.size(); i++) {
				String username = player.get(i);
				List<String> service = plugin.switchsql.QueueReward(username);

				for (int x = 0; x < service.size(); x++) {
					plugin.rewardTask.Notonline(username, service.get(x));
					plugin.switchsql.removeQueue(username, service.get(x));
				}
			}
		}
		return CommandResult.success();
	}

}
