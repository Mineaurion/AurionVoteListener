package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;

public class FakeVoteCommand  implements CommandExecutor {

    private AurionVoteListener plugin;

    public FakeVoteCommand(AurionVoteListener plugin){
        this.plugin = plugin;
    }

    public CommandResult execute(CommandSource src, CommandContext args){
        if(src.hasPermission("listener.admin")){
            Player player = args.<Player>getOne("player").get();
            String service = args.<String>getOne("service").orElse("");

            Vote vote = new Vote(service, player.getName(), "localhost", String.valueOf(System.currentTimeMillis()));
            VotifierEvent event = new VotifierEvent(vote, Cause.builder().append(player).build(EventContext.empty()));
            Sponge.getEventManager().post(event);
            plugin.sendConsoleMessage("Vote send");

            return CommandResult.success();
        }
        else{
            src.sendMessage(Text.of("You don't have permission"));
            return CommandResult.empty();
        }
    }
}
