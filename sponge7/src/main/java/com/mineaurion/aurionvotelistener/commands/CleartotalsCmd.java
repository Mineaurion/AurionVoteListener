package com.mineaurion.aurionvotelistener.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionvotelistener.Main;

public class CleartotalsCmd implements CommandExecutor {
	private Main plugin;
	public CleartotalsCmd(Main main) {
		plugin = main;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src.hasPermission("*") || src.hasPermission("listener.admin"))
		{
		Boolean clear = plugin.switchsql.Cleartotals();
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
