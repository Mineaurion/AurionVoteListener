package com.mineaurion.tjk.AurionsVoteListener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import com.mineaurion.tjk.AurionsVoteListener.Main;

public class VoteCommand implements CommandExecutor {

	@SuppressWarnings("static-access")
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Main plugin = new Main();
		if (plugin.votecommand) {
			for (int i = 0; i < plugin.voteMessage.size(); i++) {
				src.sendMessage(plugin.formatMessage(plugin.voteMessage.get(i), "", src.getName()));
			}
		}

		return CommandResult.success();
	}

}
