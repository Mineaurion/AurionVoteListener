package com.mineaurion.aurionvotelistener.sponge;

import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private AurionVoteListener plugin;
    private DataSource dataSource;

    public Utils(AurionVoteListener plugin){
        this.plugin = plugin;
        Config config = plugin.getConfig();
        AdvancedRewards configAdvancedRewards = plugin.getAdvancedRewards();
        Rewards configRewards = plugin.getRewards();
        this.dataSource = plugin.getDataSource();
    }

    public void sendVoteMessage(String serviceName, String playerName){
        Rewards configRewards = plugin.getRewards();
        Optional<Player> player = Sponge.getServer().getPlayer(playerName);
        Set<String> configServices = configRewards.services.keySet();
        Rewards.Services service;

        int votes = dataSource.totalsVote(playerName);

        if(configServices.contains(serviceName)){
            service = configRewards.services.get(serviceName);
        }
        else{
            try{
                service = configRewards.services.get("DEFAULT");
            }
            catch (Exception exception){
                player.ifPresent(p -> p.sendMessage(Text.of("The default service has been deleted")));
                plugin.getLogger().error("The default service has been deleted");
                return;
            }
        }

        if(!service.broadcast.isEmpty()){
            MessageChannel.TO_PLAYERS.send(
                    TextSerializers.FORMATTING_CODE.deserialize(formatVote(service.broadcast, serviceName, playerName, votes))
            );
        }
        if(!service.playerMessage.isEmpty()){
            player.ifPresent(p -> p.sendMessage(
                    TextSerializers.FORMATTING_CODE.deserialize(formatVote(service.playerMessage, serviceName, playerName, votes))
            ));
        }
    }

    public void sendOfflineVoteMessage(String serviceName, String playerName, int offlineVote){
        Config config = plugin.getConfig();
        if(!config.settings.offline.broadcast.isEmpty()){
            MessageChannel.TO_PLAYERS.send(
                    TextSerializers.FORMATTING_CODE.deserialize(formatOfflineVote(config.settings.offline.broadcast, serviceName, playerName, offlineVote))
            );
        }
        if(!config.settings.offline.playermessage.isEmpty()){
            Optional<Player> player = Sponge.getServer().getPlayer(playerName);
            player.ifPresent(p -> p.sendMessage(
                    TextSerializers.FORMATTING_CODE.deserialize(formatOfflineVote(config.settings.offline.playermessage, serviceName, playerName, offlineVote))
            ));
        }
    }

    public Text formatJoinMessage(String message, String player){
        message = formatName(message, player);
        int votes = dataSource.totalsVote(player);
        if (message.toLowerCase().contains("http")) {
            String url = "";
            ClickAction urlAction;
            Pattern pattern = Pattern.compile("http(\\S+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                url = matcher.group(0);
            }
            try {
                urlAction = TextActions.openUrl(new URL(url));
            } catch (MalformedURLException e) {
                plugin.getLogger().error("MalFormed Url", e);
                return Text.of("Url False, contact admin");
            }
            return TextSerializers.FORMATTING_CODE.deserialize(formatVote(message,votes)).toBuilder().onClick(urlAction).build();
        }
        else{
            return TextSerializers.FORMATTING_CODE.deserialize(formatVote(message,votes));
        }
    }


    private String formatName(String message, String playerName){
        return message
                .replaceAll("(?i)[<(\\[{](user|player|name)(name)?[>)\\]}]",playerName);
    }

    private String formatNameService(String message, String service, String player){
        return message
                .replaceAll("(?i)[<(\\[{]service(name)?[>)\\]}]", service)
                .replaceAll("(?i)[<(\\[{](user|player|name)(name)?[>)\\]}]",player);
    }

    private String formatVote(String message, String service, String player, int votes){
        return formatNameService(message, service, player)
                .replaceAll("(?i)[<(\\[{]vote(s)?[>)\\]}]", String.valueOf(votes));
    }

    private String formatVote(String message, int votes){
        return message.replaceAll("(?i)[<(\\[{]vote(s)?[>)\\]}]", String.valueOf(votes));
    }

    private String formatOfflineVote(String message, String service, String player, int votes){
        return formatVote(message, service, player, votes).replaceAll("(?i)[<(\\[{]amt?[>)\\]}]", String.valueOf(votes));
    }
}