package com.mineaurion.aurionvotelistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import ninja.leaping.configurate.ConfigurationNode;

public class Rewards {

	private Main plugin;
	public Rewards(Main main) {
		plugin = main;
	}

	public void online(String player, String service) {
		int voteTotal = plugin.switchsql.TotalsVote(player);
		long currentMiliseconde = System.currentTimeMillis();
		boolean succes = plugin.switchsql.Voted(player, voteTotal + 1, currentMiliseconde);
		Optional<Player> target = Sponge.getServer().getPlayer(player);
		List<String> reward = new ArrayList<>();
		List<String> permission = Config.permission;
		String broadcast = "";
		String playermessage = "";

		if (succes) {
			if (!plugin.config.rewardNode.getNode("services", service).isVirtual()) {
				reward = plugin.config.rewardNode.getNode("services", service, "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				broadcast = plugin.config.rewardNode.getNode("services", service, "broadcast").getString();
				playermessage = plugin.config.rewardNode.getNode("services", service, "playermessage").getString();
			} else {
				try {
					reward = plugin.config.rewardNode.getNode("services", "DEFAULT", "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					broadcast = plugin.config.rewardNode.getNode("services", "DEFAULT", "broadcast").getString();
					playermessage = plugin.config.rewardNode.getNode("services", "DEFAULT", "playermessage").getString();
				} catch (Exception e) {
					target.get().sendMessage(Text.of("The default service has been deleted"));
				}
			}
			
			for(String perm : permission) {
				if(target.get().hasPermission(perm)) {
					reward.addAll(plugin.config.adrewardNode.getNode("perms",perm).getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()));
				}
			}

			final List<String> rewardtask = reward;
			
			for (int i = 0; i < rewardtask.size(); i++) {
				final int y = i;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								rewardtask.get(y).replace("<username>", player)))
						.submit(plugin);
			}

			if (!broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(plugin.formatmessage(broadcast, service, player));
			}
			if (!playermessage.isEmpty()) {
				target.get().sendMessage(Text.of(plugin.formatmessage(playermessage, service, player)));
			}
			if (Config.AddExtraRandom) {
				random(player);
			}
			if (Config.cumulativevoting) {
				cumulative(player, voteTotal + 1);
			}
		} else {
			target.get().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}

	}

	//reward quand jouzeur co
	public void rewardoflline(String player, String service) {
		int voteTotal = plugin.switchsql.TotalsVote(player);
		long currentMiliseconde = System.currentTimeMillis();
		boolean succes = plugin.switchsql.Voted(player, voteTotal + 1, currentMiliseconde);
		Optional<Player> target = Sponge.getServer().getPlayer(player);
		List<String> reward;

		if (succes) {
			if (!plugin.config.rewardNode.getNode("services", service).isVirtual()) {
				reward = plugin.config.rewardNode.getNode("services", service, "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			} else {
				try {
					reward = plugin.config.rewardNode.getNode("services", "DEFAULT", "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				} catch (Exception e) {
					target.get().sendMessage(Text.of("The default service has been deleted"));
					return;
				}
			}

			final List<String> rewardtask = reward;
			for (int i = 0; i < rewardtask.size(); i++) {
				final int y = i;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								rewardtask.get(y).replace("<username>", player)))
						.submit(plugin);
			}

			if (Config.AddExtraRandom) {
				random(player);
			}
			if (Config.cumulativevoting) {
				cumulative(player, voteTotal + 1);
			}
		} else {
			target.get().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}

	}

	public void Notonline(String player, String service) {
		int voteTotal = plugin.switchsql.TotalsVote(player);
		long currentMiliseconde = System.currentTimeMillis();
		boolean succes = plugin.switchsql.Voted(player, voteTotal + 1, currentMiliseconde);
		List<String> reward = new ArrayList<String>();
		String broadcast = "";

		if (succes) {
			if (!plugin.config.rewardNode.getNode("services", service).isVirtual()) {
				reward = plugin.config.rewardNode.getNode("services", service, "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				broadcast = plugin.config.rewardNode.getNode("services", service, "broadcast").getString();
			} else {
				try {
					reward = plugin.config.rewardNode.getNode("services", "DEFAULT", "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					broadcast = plugin.config.rewardNode.getNode("services", "DEFAULT", "broadcast").getString();
				} catch (Exception e) {
					Sponge.getServer().getConsole().sendMessage(Text.of("The default service has been deleted"));
				}
			}

			final List<String> Rewardtask = reward;
			for (int i = 0; i < Rewardtask.size(); i++) {
				final int y = i;
				try {
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								Rewardtask.get(y).replace("<username>", player)))
						.submit(plugin);
				} catch (Exception e) {
					Sponge.getServer().getConsole().sendMessage(Text.of("The command" + Rewardtask.get(y) +" has encountered an error (Cause offline player)"));
				}
			}

			if (!broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(plugin.formatmessage(broadcast, service, player));
			}
			if (Config.AddExtraRandom) {
				random(player);
			}
			if (Config.cumulativevoting) {
				cumulative(player, voteTotal + 1);
			}
		} else {
			Sponge.getServer().getConsole().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}
	}

	public void random(String player) {
		Random r = new Random();
		float chance = r.nextFloat();
		List<String> extraramdom = Config.extrarandom;
		float value1 = 1.0f;
		float value2 = 1.0f;
		List<String> reward = new ArrayList<String>();
		String broadcast = "";
		String playermessage= "";
		boolean lucky = false;
		
		
		
		for (int i = 0; i < extraramdom.size(); i++) {			
			if ((i+1) != extraramdom.size()) {
				value1 =  (float)(Float.parseFloat(extraramdom.get(i)))/100;
				value2 =  (float)(Float.parseFloat(extraramdom.get(i+1)))/100;
			} else {
				value1 =  (float)(Float.parseFloat(extraramdom.get(i)))/100;
				value2 = 0.0f;
			}

			
			if (Config.GiveChanceReward) {
				if (chance >= value1) {
					lucky = true;
				}
			} else if (chance >= value1 && chance < value2 ) {
				lucky = true;
			}
		
			if (lucky) {
				String random = extraramdom.get(i);
				reward = plugin.config.adrewardNode.getNode("ExtraReward", random, "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				broadcast = plugin.config.adrewardNode.getNode("ExtraReward", random, "broadcast").getString();
				playermessage = plugin.config.adrewardNode.getNode("ExtraReward", random, "playermessage").getString();
	
				final List<String> Rewardtask = reward;
				for (int x = 0; x < Rewardtask.size(); x++) {
					final int y = x;
					Sponge.getScheduler().createTaskBuilder()
							.execute(() -> Sponge.getCommandManager().process(
									Sponge.getServer().getConsole().getCommandSource().get(),
									Rewardtask.get(y).replace("<username>", player)))
							.submit(plugin);
				}


				if (!broadcast.isEmpty()) {
					MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
					messageChannel.send(plugin.formatmessage(broadcast, "", player));
				}
				if (!playermessage.isEmpty()) {
					Optional<Player> target = Sponge.getServer().getPlayer(player);
					if(target.isPresent()) {
						target.get().sendMessage(
							Text.of(plugin.formatmessage(playermessage, "", player)));
					}
				}
				lucky = false;
			}
		}
	}

	public void cumulative(String player, int vote) {
		List<Integer> cumulativreward = Config.cumulativreward;
		List<String> reward = new ArrayList<String>();
		String broadcast = "";
		String playermessage = "";
		if (cumulativreward.contains(vote)) {

			reward = plugin.config.adrewardNode.getNode("cumulativevoting", String.valueOf(vote), "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			broadcast = plugin.config.adrewardNode.getNode("cumulativevoting", String.valueOf(vote), "broadcast").getString();
			playermessage = plugin.config.adrewardNode.getNode("cumulativevoting", String.valueOf(vote), "playermessage").getString();

			final List<String> Rewardtask = reward;
			for (int x = 0; x < Rewardtask.size(); x++) {
				final int y = x;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								Rewardtask.get(y).replace("<username>", player)))
						.submit(plugin);
			}
			
			if (!broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(plugin.formatmessage(broadcast, "", player));
			}
			if (!playermessage.isEmpty()) {
				Optional<Player> target = Sponge.getServer().getPlayer(player);
				if(target.isPresent()) {
					target.get().sendMessage(
						Text.of(plugin.formatmessage(playermessage, "", player)));
				}
			}
		}
	}
}
