package com.mineaurion.aurionvotelistener.sponge.commands;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import com.mineaurion.aurionvotelistener.sponge.DispatchRewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

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
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();

            List<String> queueAllPlayer = dataSource.queueAllPlayer();
            for (String player:queueAllPlayer) {
                List<String> services = dataSource.queueReward(player);
                for (String service:services) {
                    Optional<User> user = userStorage.get(player);
                    user.ifPresent( u -> {
                        u.getPlayer().ifPresent( p -> {
                            dispatchRewards.giveRewardsOffline(p, service);
                        });
                    });
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
