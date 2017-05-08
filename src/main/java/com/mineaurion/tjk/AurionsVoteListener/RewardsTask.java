package com.mineaurion.tjk.AurionsVoteListener;

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

public class RewardsTask {

	public static void online(String player, String service) {
		int VoteTotal = SwitchSQL.TotalsVote(player);
		long CurrentMiliseconde = System.currentTimeMillis();
		boolean succes = SwitchSQL.Voted(player, VoteTotal + 1, CurrentMiliseconde);
		Optional<Player> target = Sponge.getServer().getPlayer(player);
		List<String> Reward = new ArrayList<String>();
		String Broadcast;
		String playermessage;

		if (succes) {

			Reward = AurionsVoteListener.GetInstance().getNode().getNode("services", service, "commands")
					.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("services", service, "broadcast")
					.getString();
			playermessage = AurionsVoteListener.GetInstance().getNode().getNode("services", service, "playermessage")
					.getString();

			boolean Rewardempty = Reward.isEmpty();
			boolean serviceempty = Reward.isEmpty();
			boolean messageempty = Reward.isEmpty();
			if (serviceempty && Rewardempty && messageempty) {
				try {
					Reward = AurionsVoteListener.GetInstance().getNode().getNode("services", "DEFAULT", "commands")
							.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("services", "DEFAULT", "broadcast")
							.getString();
					playermessage = AurionsVoteListener.GetInstance().getNode()
							.getNode("services", "DEFAULT", "playermessage").getString();
				} catch (Exception e) {
					target.get().sendMessage(Text.of("The default service has been deleted"));
				}
			}

			for (int i = 0; i < Reward.size(); i++) {

				// if(Reward.get(i).startsWith("/")){}

				Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
						Reward.get(i).replace("<username>", player));
			}
			MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
			messageChannel.send(AurionsVoteListener.GetInstance().formatmessage(Broadcast, service, player));
			target.get().sendMessage(
					Text.of(AurionsVoteListener.GetInstance().formatmessage(playermessage, service, player)));

			if (AurionsVoteListener.GetInstance().AddExtraRandom) {
				random(player);
			}

			if (AurionsVoteListener.GetInstance().cumulativevoting) {
				cumulative(player, VoteTotal+1);
			}

		} else {
			target.get().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}

	}

	public static void Notonline(String player, String service) {
		int VoteTotal = SwitchSQL.TotalsVote(player);
		long CurrentMiliseconde = System.currentTimeMillis();
		boolean succes = SwitchSQL.Voted(player, VoteTotal + 1, CurrentMiliseconde);
		List<String> Reward = new ArrayList<String>();
		String Broadcast;

		if (succes) {

			Reward = AurionsVoteListener.GetInstance().getNode().getNode("services", service, "commands")
					.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("services", service, "broadcast")
					.getString();

			boolean Rewardempty = Reward.isEmpty();
			boolean serviceempty = Reward.isEmpty();
			if (serviceempty && Rewardempty) {
				try {
					Reward = AurionsVoteListener.GetInstance().getNode().getNode("services", "DEFAULT", "commands")
							.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("services", "DEFAULT", "broadcast")
							.getString();
				} catch (Exception e) {
					Sponge.getServer().getConsole().sendMessage(Text.of("The default service has been deleted"));
				}
			}

			for (int i = 0; i < Reward.size(); i++) {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
						Reward.get(i).replace("<username>", player));
			}
			if (AurionsVoteListener.GetInstance().broadcastoffline) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(AurionsVoteListener.GetInstance().formatmessage(Broadcast, service, player));
			}
			if (AurionsVoteListener.GetInstance().AddExtraRandom) {
				random(player);
			}
			if (AurionsVoteListener.GetInstance().cumulativevoting) {
				cumulative(player, VoteTotal);
			}

		} else {
			Sponge.getServer().getConsole().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}
	}

	public static void random(String player) {
		Random r = new Random();
		float chance = r.nextFloat();
		List<Integer> extraramdom = AurionsVoteListener.extrarandom;
		float Value1 = 0;
		float Value2 = 0;
		List<String> Reward = new ArrayList<String>();
		String Broadcast;
		String playermessage;
		boolean lucky = false;
		for (int i = 0; i <= extraramdom.size(); i++) {

			if (i == 0) {
				Value2 = (float) Float.parseFloat("0." + extraramdom.get(i));
			} else {
				if (i != extraramdom.size()) {
					Value1 = (float) Float.parseFloat("0." + extraramdom.get(i - 1));
					Value2 = (float) Float.parseFloat("0." + extraramdom.get(i));
				} else {
					Value1 = (float) Float.parseFloat("0." + extraramdom.get(i - 1));
					Value2 = 1.0f;

				}
			}

			if (AurionsVoteListener.GetInstance().GiveChanceReward) {
				if (chance >= Value1 && Value1 != 0.0f) {
					lucky = true;
				}
			} else if (chance >= Value1 && chance < Value2 && Value1 != 0.0f) {
				lucky = true;
			}

			if (lucky) {
				String random = String.valueOf(100 - extraramdom.get(i - 1));
				Reward = AurionsVoteListener.GetInstance().getNode().getNode("ExtraReward", random, "commands")
						.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("ExtraReward", random, "broadcast")
						.getString();
				playermessage = AurionsVoteListener.GetInstance().getNode()
						.getNode("ExtraReward", random, "playermessage").getString();

				for (int x = 0; x < Reward.size(); x++) {
					Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
							Reward.get(x).replace("<username>", player));
				}
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(AurionsVoteListener.GetInstance().formatmessage(Broadcast, "", player));
				Optional<Player> target = Sponge.getServer().getPlayer(player);
				target.get().sendMessage(
						Text.of(AurionsVoteListener.GetInstance().formatmessage(playermessage, "", player)));
				lucky = false;
			}

		}

	}

	public static void cumulative(String player, int vote) {
		List<Integer> cumulativreward = AurionsVoteListener.cumulativreward;
		List<String> Reward = new ArrayList<String>();
		String Broadcast;
		String playermessage;
		if (cumulativreward.contains(vote)) {
			
			Reward = AurionsVoteListener.GetInstance().getNode().getNode("cumulativevoting", String.valueOf(vote), "commands")
					.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			
			Broadcast = AurionsVoteListener.GetInstance().getNode().getNode("cumulativevoting", String.valueOf(vote), "broadcast")
					.getString();
			playermessage = AurionsVoteListener.GetInstance().getNode().getNode("cumulativevoting", String.valueOf(vote), "playermessage")
					.getString();

			for (int x = 0; x < Reward.size(); x++) {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
						Reward.get(x).replace("<username>", player));
			}
			MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
			messageChannel.send(AurionsVoteListener.GetInstance().formatmessage(Broadcast, "", player));
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			target.get()
					.sendMessage(Text.of(AurionsVoteListener.GetInstance().formatmessage(playermessage, "", player)));
		}
	}

}
