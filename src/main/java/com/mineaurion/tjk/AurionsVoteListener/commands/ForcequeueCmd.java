package com.mineaurion.tjk.AurionsVoteListener.commands;

import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.tjk.AurionsVoteListener.SwitchSQL;
import com.mineaurion.tjk.AurionsVoteListener.RewardsTask;

public class ForcequeueCmd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src.hasPermission("*") || src.hasPermission("listener.admin"))
		{
		List<String> player = SwitchSQL.QueuePlayer();
		
		for(int i = 0; i < player.size(); i++)
		{
			String username = player.get(i);
			List<String> service = SwitchSQL.QueueReward(username);
			
			for(int x = 0; x < service.size(); x++)
		    {
				RewardsTask.Notonline(username, service.get(x));
				SwitchSQL.removeQueue(username, service.get(x));
		    }
		}
		
		
	}
		return CommandResult.success();
	}

}
