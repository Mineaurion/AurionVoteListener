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

public class RewardsTask extends Main {

	public static void online(String player, String service) {
		int VoteTotal = SwitchSQL.TotalsVote(player);
		long CurrentMiliseconde = System.currentTimeMillis();
		boolean success = SwitchSQL.Voted(player, VoteTotal + 1, CurrentMiliseconde);

		Optional<Player> target = Sponge.getServer().getPlayer(player);

		List<String> Reward = new ArrayList<String>();
		List<String> Permission = Main.permission;

		String Broadcast = "";
		String playermessage = "";

		if (success) {
			if (!Main.GetInstance().getReward().getNode("services", service).isVirtual()) {
				Reward = Main.GetInstance().getReward().getNode("services", service, "commands")
						.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				Broadcast = Main.GetInstance().getReward().getNode("services", service, "broadcast")
						.getString();
				playermessage = Main.GetInstance().getReward()
						.getNode("services", service, "playermessage").getString();
			} else {
				try {
					Reward = Main.GetInstance().getReward().getNode("services", "DEFAULT", "commands").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					Broadcast = Main.GetInstance().getReward().getNode("services", "DEFAULT", "broadcast").getString();
					playermessage = Main.GetInstance().getReward().getNode("services", "DEFAULT", "playermessage").getString();
				} catch (Exception e) {
					target.get().sendMessage(Text.of("The default service has been deleted"));
				}
			}
			for(String perm : Permission) {
				if(target.get().hasPermission(perm)) {
					Reward.addAll(Main.GetInstance().getAdReward().getNode("perms",perm).getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()));
				}
			}
			final List<String> Rewardtask = Reward;
			for (int i = 0; i < Rewardtask.size(); i++) {
				final int y = i;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								Rewardtask.get(y).replace("<username>", player)))
						.submit(Main.GetInstance());
			}
			if (!Broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(Main.GetInstance().formatMessage(Broadcast, service, player));
			}
			if (!playermessage.isEmpty()) {
				target.get().sendMessage(
						Text.of(Main.GetInstance().formatMessage(playermessage, service, player)));
			}
			if (Main.AddExtraRandom) {
				random(player);
			}
			if (Main.cumulativevoting) {
				cumulative(player, VoteTotal + 1);
			}

		} else {
			target.get().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}

	}

	public static void rewardoflline(String player, String service) {
		int VoteTotal = SwitchSQL.TotalsVote(player);
		long CurrentMiliseconde = System.currentTimeMillis();
		boolean succes = SwitchSQL.Voted(player, VoteTotal + 1, CurrentMiliseconde);
		Optional<Player> target = Sponge.getServer().getPlayer(player);
		List<String> Reward = new ArrayList<String>();

		if (succes) {
			if (!Main.GetInstance().getReward().getNode("services", service).isVirtual()) {
				Reward = Main.GetInstance().getReward().getNode("services", service, "commands")
						.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
			} else {
				try {
					Reward = Main.GetInstance().getReward().getNode("services", "DEFAULT", "commands")
							.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				} catch (Exception e) {
					target.get().sendMessage(Text.of("The default service has been deleted"));
					return;
				}
			}
			final List<String> Rewardtask = Reward;
			for (int i = 0; i < Rewardtask.size(); i++) {
				final int y = i;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								Rewardtask.get(y).replace("<username>", player)))
						.submit(Main.GetInstance());
			}
			if (Main.AddExtraRandom) {
				random(player);
			}
			if (Main.cumulativevoting) {
				cumulative(player, VoteTotal + 1);
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
		String Broadcast = "";
		if (succes) {
			if (!Main.GetInstance().getReward().getNode("services", service).isVirtual()) {
				Reward = Main.GetInstance().getReward().getNode("services", service, "commands")
						.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				Broadcast = Main.GetInstance().getReward().getNode("services", service, "broadcast")
						.getString();
			} else {
				try {
					Reward = Main.GetInstance().getReward().getNode("services", "DEFAULT", "commands")
							.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
					Broadcast = Main.GetInstance().getReward().getNode("services", "DEFAULT", "broadcast")
							.getString();
				} catch (Exception e) {
					Sponge.getServer().getConsole().sendMessage(Text.of("The default service has been deleted"));
				}
			}

			final List<String> Rewardtask = Reward;
			for (int i = 0; i < Rewardtask.size(); i++) {
				final int y = i;
				Sponge.getScheduler().createTaskBuilder()
						.execute(() -> Sponge.getCommandManager().process(
								Sponge.getServer().getConsole().getCommandSource().get(),
								Rewardtask.get(y).replace("<username>", player)))
						.submit(Main.GetInstance());
			}
			if (!Broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(Main.GetInstance().formatMessage(Broadcast, service, player));
			}
			if (Main.AddExtraRandom) {
				random(player);
			}
			if (Main.cumulativevoting) {
				cumulative(player, VoteTotal + 1);
			}
		} else {
			Sponge.getServer().getConsole().sendMessage(Text.of("A problem has occurred, please contact an admin"));
		}
	}

	public static void random(String player) {
		Random r = new Random();
		float chance = r.nextFloat();
		List<Integer> extraramdom = Main.extrarandom;
		float Value1 = 0.0f;
		float Value2 = 0;
		List<String> Reward = new ArrayList<String>();
		String Broadcast;
		String playermessage;
		boolean lucky = false;
		
		Optional<Player> target1 = Sponge.getServer().getPlayer(player);
		target1.get().sendMessage(
				Text.of(String.valueOf(extraramdom.size())));
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

			if (Main.GiveChanceReward) {
				if (chance >= Value1 && Value1 != 0.0f) {
					lucky = true;
				}
			} else if (chance >= Value1 && chance < Value2 && Value1 != 0.0f || chance == Value1 ) {
				lucky = true;
			}
			if (lucky) {
				String random = String.valueOf(100 - extraramdom.get(i - 1));
				Reward = Main.GetInstance().getReward().getNode("ExtraReward", random, "commands")
						.getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				Broadcast = Main.GetInstance().getReward().getNode("ExtraReward", random, "broadcast").getString();
				playermessage = Main.GetInstance().getReward().getNode("ExtraReward", random, "playermessage").getString();

				for (int x = 0; x < Reward.size(); x++) {
					Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
							Reward.get(x).replace("<username>", player));
				}
				if (!Broadcast.isEmpty()) {
					MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
					messageChannel.send(Main.GetInstance().formatMessage(Broadcast, "", player));
				}
				if (!playermessage.isEmpty()) {
					Optional<Player> target = Sponge.getServer().getPlayer(player);
					target.get().sendMessage(
							Text.of(Main.GetInstance().formatMessage(playermessage, "", player)));
				}
				lucky = false;
			}
		}
	}

	public static void cumulative(String player, int vote) {
		List<Integer> cumulativreward = Main.cumulativreward;
		List<String> Reward = new ArrayList<String>();
		String Broadcast;
		String playermessage;
		if (cumulativreward.contains(vote)) {
			Reward = Main.GetInstance().getReward()
					.getNode("cumulativevoting", String.valueOf(vote), "commands").getChildrenList().stream()
					.map(ConfigurationNode::getString).collect(Collectors.toList());
			Broadcast = Main.GetInstance().getReward()
					.getNode("cumulativevoting", String.valueOf(vote), "broadcast").getString();
			playermessage = Main.GetInstance().getReward()
					.getNode("cumulativevoting", String.valueOf(vote), "playermessage").getString();

			for (int x = 0; x < Reward.size(); x++) {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
						Reward.get(x).replace("<username>", player));
			}
			if (!Broadcast.isEmpty()) {
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
				messageChannel.send(Main.GetInstance().formatMessage(Broadcast, "", player));
			}
			if (!playermessage.isEmpty()) {
				Optional<Player> target = Sponge.getServer().getPlayer(player);
				target.get().sendMessage(
						Text.of(Main.GetInstance().formatMessage(playermessage, "", player)));
			}
		}
	}
}
