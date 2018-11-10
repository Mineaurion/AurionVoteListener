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
    private Config config;
    private DispatchRewards dispatchRewards;
    private DataSource dataSource;

    public EventManager(AurionVoteListener plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.dispatchRewards = plugin.getDispatchRewards();
        this.dataSource = plugin.getDataSource();
    }

    @Listener
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        String votePlayer = vote.getUsername();
        Optional<Player> target = Sponge.getServer().getPlayer(votePlayer);
        String player = target.map(Player::getName).orElse(votePlayer);
        dispatchRewards.giveRewards(player, vote.getServiceName());
        if(!target.isPresent()){
            plugin.sendConsoleMessage("The " + player + " is not connected, try to give reward");
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event){
        Player player = event.getTargetEntity();
        String username = player.getName();

        if(dataSource.queueUsername(username)){
            List<String> queueReward = dataSource.queueReward(username);
            if(!queueReward.isEmpty()){
                for (String vote:queueReward) {
                    dispatchRewards.giveRewards(username, vote);
                    dataSource.removeQueue(username, vote);
                }
            }
        }
        if(config.settings.joinMessage){
            for (String message: config.joinmessage) {
                player.sendMessage(plugin.getUtils().formatJoinMessage(message, username));
            }
        }
    }
}
