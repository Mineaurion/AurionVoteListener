package com.mineaurion.tjk.AurionsVoteListener.commands;

import com.mineaurion.tjk.AurionsVoteListener.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.tjk.AurionsVoteListener.SwitchSQL;

public class VoteTopCmd implements CommandExecutor {
	
	@SuppressWarnings("static-access")
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Main plugin = new Main();
		
			for(int i = 0; i<plugin.votetopheader.size();i++){
				src.sendMessage(plugin.formatMessage(plugin.votetopheader.get(i),"",src.getName()));
			}
			SwitchSQL.VoteTop(src);
			return CommandResult.success();
		}
}
