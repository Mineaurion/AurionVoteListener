package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializers;

public class VoteTopCommand implements CommandExecutor {

    private AurionVoteListener plugin;

    public VoteTopCommand(AurionVoteListener plugin){
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args){
        Config config = plugin.getConfig();
        for(String message: config.settings.voteTop.header){
            src.sendMessage(plugin.getUtils().formatJoinMessage(message, src.getName()));
        }
        String voteTop = plugin.getDataSource().voteTop();

        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(voteTop));
        return CommandResult.success();
    }
}
