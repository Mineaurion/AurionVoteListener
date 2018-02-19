package com.mineaurion.tjk.AurionsVoteListener.commands;



import com.mineaurion.tjk.AurionsVoteListener.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;

public class FakeVoteCommand implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if ((src.hasPermission("*")) || (src.hasPermission("listener.admin")))
		{
			Player player = args.<Player>getOne("player").get();
			String service = args.<String>getOne("service").orElse("");
			
			if(service == ""){
				Vote vote = new Vote("DEFAULT", player.getName(), "localhost", String.valueOf(System.currentTimeMillis()));
	            VotifierEvent event = new VotifierEvent(vote, Cause.builder().append(vote).append(Main.GetInstance()).build(EventContext.empty()));
	            
	            Sponge.getEventManager().post(event);
				Sponge.getServer().getConsole().sendMessage(Text.of("Vote send"));
			}
			else{
				Vote vote = new Vote(service, player.getName(), "localhost", String.valueOf(System.currentTimeMillis()));
	            VotifierEvent event = new VotifierEvent(vote, Cause.builder().append(vote).append(Main.GetInstance()).build(EventContext.empty()));
	            Sponge.getEventManager().post(event);
				Sponge.getServer().getConsole().sendMessage(Text.of("Vote send"));
			}
		}
		return CommandResult.success();
	}

}
