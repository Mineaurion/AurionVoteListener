package com.mineaurion.aurionvotelistener.sponge;

import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import java.sql.SQLException;
import java.util.*;

public class DispatchRewards {

    private AurionVoteListener plugin;
    private DataSource dataSource;
    private Utils utils;

    public DispatchRewards(AurionVoteListener plugin){
        this.plugin = plugin;
        this.dataSource = plugin.getDataSource();
        this.utils = plugin.getUtils();
    }

    public void giveRewards(String player, String service){
        Config config = plugin.getConfig();
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Rewards configRewards = plugin.getRewards();
        int voteTotal = dataSource.totalsVote(player);
        long currentMs = System.currentTimeMillis();
        Optional<Player> target = Sponge.getServer().getPlayer(player);
        List<String> rewards = new ArrayList<String>();
        Set<String> permission = configAdvancedRewards.perms.keySet();
        Set<String> configServices = configRewards.services.keySet();
        try{
            dataSource.voted(player, voteTotal + 1, currentMs);
            //Ajout reward
            if(configServices.contains(service)){
                rewards = configRewards.services.get(service).commands;
            }
            else{
                try{
                    rewards = configRewards.services.get("DEFAULT").commands;
                }
                catch (Exception exception){
                    target.ifPresent(p -> p.sendMessage(Text.of("The Default service has been deleted")));
                }
            }

            //Ajout des rewards par permission
            for(String perm: permission){
                if(target.isPresent() && target.get().hasPermission(perm)){
                    rewards.addAll(configAdvancedRewards.perms.get(perm).commands);
                }
            }

            final List<String> rewardTask = rewards;
            if (target.isPresent()){
                sendRewards(rewardTask, service, target.get());
                //Envoie des random rewards
                if(config.settings.addExtraReward){
                    random(player);
                }
                //Envoie des rewards cumulatifs
                if(config.settings.cumulativevoting){
                    cumulative(player, voteTotal + 1);
                }
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL Error", e);

        }
    }

    private void random(String player){
        Config config = plugin.getConfig();
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Map<Integer, AdvancedRewards.ExtraReward> extraReward = configAdvancedRewards.extraReward;
        Random r = new Random();
        float chance = r.nextFloat();
        List<Integer> extraRandom = new ArrayList<>(configAdvancedRewards.extraReward.keySet());
        extraRandom.sort(Collections.reverseOrder());
        float value1 = 1.0f;
        float value2 = 1.0f;
        List<String> reward;
        boolean lucky = false;

        for(int i = 0; i < extraRandom.size(); i++){
            if((i+1) != extraRandom.size()){
                value1 = extraRandom.get(i) / 100;
                value2 = extraRandom.get(i+1)/100;
            } else{
              value1 = extraRandom.get(i) / 100;
              value2 = 0.0f;
            }

            if(config.settings.giveChanceReward){
                if(chance >= value1){
                    lucky = true;
                }
            } else if(chance >= value1 && chance < value2){
                lucky = false;
            }

            if(lucky){
                Integer random = extraRandom.get(i);
                reward        = extraReward.get(random).commands;

                final List<String> rewardTask = reward;
                sendRewards(rewardTask,"", player);
                lucky = false;
            }
        }
    }

    private void cumulative(String player, int vote){
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Set<Integer> cumulativeReward = configAdvancedRewards.cumulativeVoting.keySet();
        Map<Integer,AdvancedRewards.ExtraReward> extraReward = configAdvancedRewards.extraReward;
        List<String> reward;
        if(cumulativeReward.contains(vote)){
            reward        = extraReward.get(vote).commands;
            final List<String> rewardTask = reward;
            sendRewards(rewardTask, "", player);
            // TODO : check le format de message envoyé pour les advanceds
        }
    }

    private void sendRewards(List<String> rewards, String service, Player player){
        String playerName = player.getName();
        for(int i = 0; i < rewards.size(); i++){
            final int j = i;
            Sponge.getScheduler().createTaskBuilder()
                    .execute(() -> Sponge.getCommandManager().process(
                            Sponge.getServer().getConsole().getCommandSource().get(),
                            rewards.get(j).replace("<username>", playerName)
                    ))
                    .submit(plugin);
        }
        utils.sendVoteMessage(service, playerName);
    }

    private void sendRewards(List<String> rewards, String service, String player){
        Config config = plugin.getConfig();
        for(int i = 0; i < rewards.size(); i++){
            final int j = i;
            Sponge.getScheduler().createTaskBuilder()
                    .execute(() -> Sponge.getCommandManager().process(
                            Sponge.getServer().getConsole().getCommandSource().get(),
                            rewards.get(j).replace("<username>", player)
                    ))
                    .submit(plugin);
        }
        if(config.settings.offline.enable){
            utils.sendOfflineVoteMessage(service, player, rewards.size());
        }
    }
}
