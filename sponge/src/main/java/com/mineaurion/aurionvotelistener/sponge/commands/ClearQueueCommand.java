package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class ClearQueueCommand implements CommandExecutor {

    private AurionVoteListener plugin;
    private DataSource dataSource;

    public ClearQueueCommand(AurionVoteListener plugin){
        this.plugin = plugin;
        this.dataSource = plugin.getDataSource();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args){
        if(src.hasPermission("listener.admin")){
            dataSource.clearQueue();
            src.sendMessage(Text.of("Cleared successfull"));
            return CommandResult.success();
        }
        else{
            src.sendMessage(Text.of("You don't have permission"));
            return CommandResult.empty();
        }
    }
}
