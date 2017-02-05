package com.mineaurion.tjk.AurionsVoteListener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.mineaurion.tjk.AurionsVoteListener.SwitchSQL;

public class CleartotalsCmd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src.hasPermission("*") || src.hasPermission("listener.admin"))
		{
		Boolean clear = SwitchSQL.Cleartotals();
		if(clear){
			src.sendMessage(Text.of("Cleared successful"));
			return CommandResult.success();
		}
		else{
			src.sendMessage(Text.of("Cleared fail"));
			return CommandResult.empty();
		}
		}else{
			src.sendMessage(Text.of("You don't have permission"));
		return CommandResult.empty();
		}
		
	}

}
