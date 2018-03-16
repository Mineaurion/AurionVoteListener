package com.mineaurion.aurionVoteListener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.aurionVoteListener.Config;
import com.mineaurion.aurionVoteListener.Main;



public class VoteCommand implements CommandExecutor {
private Main plugin;
	public VoteCommand(Main main) {
		plugin = main;
	}

	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (Config.votecommand) {
			for (int i = 0; i < Config.voteMessage.size(); i++) {
				src.sendMessage(plugin.formatmessage(Config.voteMessage.get(i), "", src.getName()));
			}
		}
		return CommandResult.success();
	}

}
