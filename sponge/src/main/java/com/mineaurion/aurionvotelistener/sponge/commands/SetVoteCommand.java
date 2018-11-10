package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class SetVoteCommand implements CommandExecutor {
    private AurionVoteListener plugin;

    public SetVoteCommand(AurionVoteListener plugin){
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args){
        if(src.hasPermission("listener.admin")){
            String player = args.<String>getOne("player").get();
            int vote = args.<Integer>getOne("vote").get();
            long currentMs = System.currentTimeMillis();
            try{
                plugin.getDataSource().voted(player, vote, currentMs);
                src.sendMessage(Text.of("Set vote successfull"));
                return CommandResult.success();
            }
            catch(SQLException exception){
                plugin.getLogger().error("SQL Exception", exception);
                src.sendMessage(Text.of("SetVote fail, check console"));
                return CommandResult.empty();
            }
        }
        else{
            src.sendMessage(Text.of("You don't have permission"));
            return CommandResult.empty();
        }
    }
}
