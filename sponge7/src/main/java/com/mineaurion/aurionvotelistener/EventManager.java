package com.mineaurion.aurionvotelistener;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;

public class EventManager {

	private Main plugin;
	public EventManager(Main main) {
		plugin = main;
	}

	
	@Listener
	public void onVote(VotifierEvent event) throws SQLException {
		Vote vote = event.getVote();
		String player = vote.getUsername();

		if (Config.onlineOnly) {
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			if (target.isPresent()) {
				String players = target.get().getName();
				String voteName = vote.getServiceName();
				plugin.rewardTask.online(players, voteName);
			} else {
				plugin.switchsql.offline(vote.getUsername(), vote.getServiceName(), vote.getTimeStamp(), vote.getAddress());
				Sponge.getServer().getConsole().sendMessage(Text.of("The player is not connected"));
			}
		} else {
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			if (target.isPresent()) {
				String players = target.get().getName();
				String voteName = vote.getServiceName();
				plugin.rewardTask.online(players, voteName);
			} else {
				plugin.rewardTask.Notonline(player, vote.getServiceName());
				Sponge.getServer().getConsole().sendMessage(Text.of("The player is not connected, try to give reward"));
			}
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) throws SQLException {
		Player player = (Player) event.getTargetEntity();
		String username = player.getName();
		if ((Config.SQLType == "MySQL") && (Config.dbHost.isEmpty() || Config.dbHost == null || Config.dbUser.isEmpty() || Config.dbUser == null || Config.dbPass.isEmpty() || Config.dbPass == null)) {
			if (player.hasPermission("*") || player.hasPermission("listener.top")) {
				player.sendMessage(Text.builder("<AurionsVoteListener> Please config Database.").color(TextColors.RED).build());
			}
		} else {
			if (plugin.switchsql.QueueUsername(username)) {

				List<String> service = plugin.switchsql.QueueReward(username);
				int totalVote = service.size();
				for (int i = 0; i < totalVote; i++) {
					plugin.rewardTask.rewardoflline(username, service.get(i));
					plugin.switchsql.removeQueue(username, service.get(i));
				}
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;

				if(!Config.offlineBroadcast.isEmpty()) {
					messageChannel.send(plugin.formatmessage(Config.offlineBroadcast.replace("<amt>", String.valueOf(totalVote)), "", username));
				}
				if(!Config.offlinePlayerMessage.isEmpty()) {
					player.sendMessage(Text.of(plugin.formatmessage(Config.offlinePlayerMessage.replace("<amt>", String.valueOf(totalVote)), "", username)));
				} 
			}
			if (Config.joinmessage) {
				for (int i = 0; i < Config.messagejoin.size(); i++) {
					player.sendMessage(plugin.formatmessage(Config.messagejoin.get(i), "", username));
				}
			}
		}
	}
}
