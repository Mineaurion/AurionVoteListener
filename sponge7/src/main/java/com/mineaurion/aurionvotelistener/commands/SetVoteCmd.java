package com.mineaurion.aurionvotelistener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionvotelistener.Main;

public class SetVoteCmd implements CommandExecutor {
	private Main plugin;
	public SetVoteCmd(Main main) {
		plugin = main;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if ((src.hasPermission("*")) || (src.hasPermission("listener.admin")))
		{
			String player = args.<String>getOne("player").get();
			int vote = args.<Integer>getOne("vote").get();	
			long CurrentMiliseconde = System.currentTimeMillis();
			boolean succes = plugin.switchsql.Voted(player, vote, CurrentMiliseconde);
			if(succes){
				src.sendMessage(Text.of("Vote set for "+player+" : "+String.valueOf(vote)));
				return CommandResult.success();
			}else{
				src.sendMessage(Text.of("an error has occurred"));
				return CommandResult.success();
			}
		}else{
			src.sendMessage(Text.of("You don't have permission"));
		return CommandResult.empty();
		}
	}

}
