package com.mineaurion.aurionvotelistener.sponge;

import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.List;
import java.util.Optional;

public class EventManager {

    private AurionVoteListener plugin;
    private DispatchRewards dispatchRewards;
    private DataSource dataSource;

    public EventManager(AurionVoteListener plugin){
        this.plugin = plugin;
        this.dispatchRewards = plugin.getDispatchRewards();
        this.dataSource = plugin.getDataSource();
    }

    @Listener
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        String votePlayer = vote.getUsername();
        Optional<Player> target = Sponge.getServer().getPlayer(votePlayer);
        String player = target.map(Player::getName).orElse(votePlayer);

        if(target.isPresent()){
            dispatchRewards.giveRewards(player, vote.getServiceName());
        }
        else{
            plugin.sendConsoleMessage("The " + player + " is not connected, try to give reward");
            dataSource.offline(player, vote.getServiceName(), String.valueOf(System.currentTimeMillis()), vote.getAddress());
        }

    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event){
        Config config = plugin.getConfig();
        Player player = event.getTargetEntity();
        String username = player.getName();
        if(config.settings.queueVote){
            if(dataSource.queueUsername(username)){
                List<String> queueReward = dataSource.queueReward(username);
                if(!queueReward.isEmpty()){
                    for (String vote:queueReward) {
                        dispatchRewards.giveRewards(username, vote);
                        dataSource.removeQueue(username, vote);
                    }
                }
            }
        }
        if(config.settings.join.enable){
            for (String message: config.settings.join.message) {
                player.sendMessage(plugin.getUtils().formatJoinMessage(message, username));
            }
        }
    }
}
