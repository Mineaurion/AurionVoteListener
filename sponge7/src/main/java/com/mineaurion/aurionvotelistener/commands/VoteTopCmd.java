package com.mineaurion.aurionvotelistener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.aurionvotelistener.Config;
import com.mineaurion.aurionvotelistener.Main;

public class VoteTopCmd implements CommandExecutor {
	private Main plugin;

	public VoteTopCmd(Main main) {
		plugin = main;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		for (int i = 0; i < Config.votetopheader.size(); i++) {
			src.sendMessage(plugin.formatmessage(Config.votetopheader.get(i), "", src.getName()));
		}
		plugin.switchsql.VoteTop(src);

		return CommandResult.success();
	}
}
