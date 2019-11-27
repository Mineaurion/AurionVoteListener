package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

    private AurionVoteListener plugin;
    public CommandSpec listenerCommand;
    public CommandSpec voteCommand;
    public CommandSpec voteTopCommand;

    public CommandManager(AurionVoteListener plugin){
        this.plugin = plugin;
        Init();
    }

    private void Init(){

        CommandSpec fakeVoteCommand = CommandSpec.builder()
                .permission("listener.admin")
                .description(Text.of("Send a fakevote"))
                .arguments(GenericArguments.player(Text.of("player")), GenericArguments.optional(GenericArguments.string(Text.of("service"))))
                .executor(new FakeVoteCommand(plugin))
                .build();

        CommandSpec clearQueueCommand = CommandSpec.builder()
                .permission("listener.admin")
                .description(Text.of("Clear the queue in database"))
                .executor(new ClearQueueCommand(plugin))
                .build();

        CommandSpec clearTotalsCommand = CommandSpec.builder()
                .permission("listener.admin")
                .description(Text.of("Clear the totals in database"))
                .executor(new ClearTotalsCommand(plugin))
                .build();

        CommandSpec forceQueueCommand = CommandSpec.builder()
                .permission("listnener.admin")
                .description(Text.of("Clear the queue by executing the vote"))
                .executor(new ForceQueueCommand(plugin))
                .build();

        CommandSpec setVoteCommand = CommandSpec.builder()
                .permission("listener.admin")
                .description(Text.of("Set vote of player"))
                .arguments(GenericArguments.string(Text.of("player")), GenericArguments.integer(Text.of("vote")))
                .executor(new SetVoteCommand(plugin))
                .build();

        listenerCommand = CommandSpec.builder()
                .permission("listener.admin")
                .description(Text.of("Plugin management"))
                .child(clearTotalsCommand, "cleartotals")
                .child(clearQueueCommand, "clearqueue")
                .child(forceQueueCommand, "forcequeue")
                .child(fakeVoteCommand, "fakevote")
                .child(setVoteCommand, "set")
                .build();

        voteCommand = CommandSpec.builder()
                .description(Text.of("Vote Command"))
                .executor(new VoteCommand(plugin))
                .build();

        voteTopCommand = CommandSpec.builder()
                .description(Text.of("Vote Top Command"))
                .executor(new VoteTopCommand(plugin))
                .build();
    }
}
