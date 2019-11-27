package com.mineaurion.aurionvotelistener.sponge;

import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

import static java.lang.Integer.parseInt;

public class DispatchRewards {

    private AurionVoteListener plugin;
    private DataSource dataSource;
    private Utils utils;

    public DispatchRewards(AurionVoteListener plugin){
        this.plugin = plugin;
        this.dataSource = plugin.getDataSource();
        this.utils = plugin.getUtils();
    }

    public List<ServiceRewardConfig> getRewards(Player player, String service){
        List<ServiceRewardConfig> rewards = new ArrayList<ServiceRewardConfig>();

        Config config = plugin.getConfig();
        Rewards configRewards = plugin.getRewards();
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        //Retourne les clefs du tableau de permission
        Set<String> permissionsRewards = configAdvancedRewards.perms.keySet();
        //Retourne les clefs des rewards classiques
        Set<String> configServices = configRewards.services.keySet();
        //Vote actuel du joueur avant l'execution de recompense
        int voteTotal = dataSource.totalsVote(player.getName());
        //On rajoute un vote au joueur
        dataSource.voted(player.getName(), voteTotal + 1, System.currentTimeMillis());

        //Retourne un reward suivant le service config ou celui par defaut
        String getService = "DEFAULT";
        if(configServices.contains(service)){
            getService = service;

        }

        if(configRewards.services.get(getService) != null){
            ServiceRewardConfig rewardService = new ServiceRewardConfig(configRewards.services.get(getService), getService);
            rewards.add(rewardService);
        }
        else{
            plugin.getLogger().error("[AurionsVoteListener] Something is wrong with the config, the service " + getService + " was not found");
        }
        //Ajout du reward

        //Ajout les rewards base sur la change
        if(config.settings.chanceReward){
            rewards.add(new ServiceRewardConfig(random(player)));
        }
        //Ajout des rewards cumulatifs
        if(config.settings.cumulativeReward){
            AdvancedRewards.ExtraServices cumulative = cumulative(player, voteTotal + 1);
            if(cumulative != null){
                rewards.add(new ServiceRewardConfig(cumulative));
            }
        }
        if(config.settings.permissionReward){
            for (String perm: permissionsRewards){
                if(player.hasPermission(perm)){
                    rewards.add(new ServiceRewardConfig(configAdvancedRewards.perms.get(perm)));
                }
            }
        }
        return rewards;

    }

    public void giveRewards(Player player, String service){
        List<ServiceRewardConfig> rewards = getRewards(player, service);
        for (ServiceRewardConfig reward: rewards) {
            sendCommands(reward.commands, player);
            utils.sendVoteMessage(reward.broadcast, reward.playerMessage, reward.serviceName, player.getName());
        }
    }

    public void giveRewardsOffline(Player player, String service){
        List<ServiceRewardConfig> rewards = getRewards(player, service);
        int loop = 0;
        for(ServiceRewardConfig reward: rewards){
            sendCommands(reward.commands, player);
            loop += 1;
        }
        utils.sendOfflineVoteMessage(service, player.getName(), loop);
    }

    private AdvancedRewards.ExtraServices random(Player player){
        Config config = plugin.getConfig();
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Map<Integer, AdvancedRewards.ExtraServices> extraReward = configAdvancedRewards.extraReward;
        Random r = new Random();
        float chance = r.nextFloat();
        WeightedRandom<String> extraWeightedRandom = new WeightedRandom<>();
        //List<Integer> extraRandom = new ArrayList<>(configAdvancedRewards.extraReward.keySet());

        for(Integer extraRandom: configAdvancedRewards.extraReward.keySet()){
            extraWeightedRandom.addEntry(extraRandom.toString(), extraRandom);
        }

        //if(config.settings.chanceReward){
        return extraReward.get(parseInt(extraWeightedRandom.getRandom()));
        //sendRewards(reward, "", player);

    }

    private AdvancedRewards.ExtraServices cumulative(Player player, int vote){
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Set<Integer> cumulativeReward = configAdvancedRewards.cumulativeVoting.keySet();
        Map<Integer, AdvancedRewards.ExtraServices> cumulativeVoting = configAdvancedRewards.cumulativeVoting;
        if(cumulativeReward.contains(vote)){
            //sendRewards(rewardTask, "", player);
            return cumulativeVoting.get(vote);
        }
        return null;
    }

    private void sendCommands(List<String> commands, Player player){
        String playerName = player.getName();
        for(int i = 0; i < commands.size(); i++){
            final int j = i;
            Sponge.getScheduler().createTaskBuilder()
                    .execute(() -> Sponge.getCommandManager().process(
                            Sponge.getServer().getConsole().getCommandSource().get(),
                            commands.get(j).replace("<username>", playerName)
                    ))
                    .submit(plugin);
        }
    }
}
