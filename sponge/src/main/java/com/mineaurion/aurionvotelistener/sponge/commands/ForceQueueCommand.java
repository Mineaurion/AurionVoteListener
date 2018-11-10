package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import com.mineaurion.aurionvotelistener.sponge.DispatchRewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.List;

public class ForceQueueCommand implements CommandExecutor {

    private AurionVoteListener plugin;
    private DataSource dataSource;
    private DispatchRewards dispatchRewards;

    public ForceQueueCommand(AurionVoteListener plugin){
        this.plugin = plugin;
        this.dataSource = plugin.getDataSource();
        this.dispatchRewards = plugin.getDispatchRewards();
    }
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args){
        if(src.hasPermission("listener.admin")){
            List<String> queueAllPlayer = dataSource.queueAllPlayer();
            for (String player:queueAllPlayer) {
                List<String> services = dataSource.queueReward(player);
                for (String service:services) {
                    dispatchRewards.giveRewards(player,service);
                    dataSource.removeQueue(player,service);
                }
            }
            return CommandResult.success();
        }
        else{
            src.sendMessage(Text.of("You don't have this permission"));
            return CommandResult.empty();
        }
    }
}
