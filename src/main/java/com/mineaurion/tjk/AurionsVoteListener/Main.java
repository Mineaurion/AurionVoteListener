package com.mineaurion.tjk.AurionsVoteListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.inject.Inject;
import com.mineaurion.tjk.AurionsVoteListener.commands.ClearqueueCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.CleartotalsCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.FakeVoteCommand;
import com.mineaurion.tjk.AurionsVoteListener.commands.ForcequeueCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.SetVoteCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.VoteCommand;
import com.mineaurion.tjk.AurionsVoteListener.commands.VoteTopCmd;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = Main.AURIONS_ID, name = "Main", version = "1.3", authors = {
		"THEJean_Kevin" }, description = "A votifier listener for Sponge", dependencies = {
				@Dependency(id = "nuvotifier", optional = true) })
public class Main {

	public int version = 9;
	public static boolean old = false;

	@Inject
	Game game;

	@Inject
	Logger logger;

	public Logger getLogger() {
		return logger;
	}

	@Inject
	private PluginContainer plugin;

	@Inject
	@ConfigDir(sharedRoot = false)
	public Path defaultConfig;

	public static ConfigurationLoader<CommentedConfigurationNode> settingLoader;
	public CommentedConfigurationNode settingNode;

	public static ConfigurationLoader<CommentedConfigurationNode> rewardLoader;
	public CommentedConfigurationNode rewardNode;

	public static ConfigurationLoader<CommentedConfigurationNode> adrewardLoader;
	public CommentedConfigurationNode adrewardNode;

	public CommentedConfigurationNode getSetting() {
		return this.settingNode;
	}
	public CommentedConfigurationNode getReward() {
		return this.rewardNode;
	}
	public CommentedConfigurationNode getAdReward() {
		return this.adrewardNode;
	}

	public boolean newConfig = false;

	// global
	private static Main instance;
	private updateconfig updateconfig;
	public static String SQLType;
	public static String SQLFile;
	public final static String AURIONS_ID = "aurionsvotelistener";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	public Task task;

	// Settings
	public static boolean onlineOnly = true;
	public static boolean broadcastoffline = false;
	public static boolean votecommand = true;
	public static boolean joinmessage = true;
	public static String dbHost;
	public static int dbPort = 3306;
	public static String dbUser;
	public static String dbPass;
	public static String dbName;
	public static String dbPrefix;
	public static String dbTableTotal = "ListenerTotal";
	public static String dbTableQueue = "ListenerQueue";
	public static int votetopnumber = 10;
	public static boolean AddExtraRandom = false;
	public static boolean GiveChanceReward = true;
	public static List<Integer> extrarandom = new ArrayList<Integer>();
	public static List<Integer> cumulativreward = new ArrayList<Integer>();
	public static List<String> permission = new ArrayList<String>();
	public static int delay = 300;
	public static boolean cumulativevoting = false;

	// Message
	public static List<String> voteMessage = new ArrayList<String>();
	public static List<String> messagejoin = new ArrayList<String>();
	public static List<String> annoucement = new ArrayList<String>();
	public static String offlineBroadcast;
	public static String offlinePlayerMessage;

	// votetop
	public static String votetopformat = "<POSITION>. <GREEN><username> - <WHITE><TOTAL>";
	public static List<String> votetopheader = new ArrayList<String>();

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Main.instance = this;
		updateconfig = new updateconfig();
		settingLoader = HoconConfigurationLoader.builder().setPath(Paths.get(defaultConfig + "/Setting.conf")).build();
		rewardLoader = HoconConfigurationLoader.builder().setPath(Paths.get(defaultConfig + "/Reward.conf")).build();
		adrewardLoader = HoconConfigurationLoader.builder().setPath(Paths.get(defaultConfig + "/AdvancedReward.conf")).build();

		getLogger().info("Main Vote loading...");
		getLogger().info("Trying To setup Config Loader");

		Asset configAsset = plugin.getAsset("Setting.conf").get();
		Asset RewardAsset = plugin.getAsset("Reward.conf").get();
		Asset AdRewardAsset = plugin.getAsset("AdvancedReward.conf").get();

		// Directory
		if (!Files.exists(Paths.get(defaultConfig + "/Setting.conf"))
				&& !Files.exists(Paths.get(defaultConfig + "/Reward.conf"))
				&& !Files.exists(Paths.get(defaultConfig + "/AdvancedReward.conf"))) {
			if (configAsset != null && RewardAsset != null && AdRewardAsset != null) {
				if (Paths.get(defaultConfig + "/aurionsvotelistener.conf").toFile().exists()) {
					newConfig = true;
				} else {
					try {
						getLogger().info("Copying Default Config");
						configAsset.copyToDirectory(defaultConfig);
						RewardAsset.copyToDirectory(defaultConfig);
						AdRewardAsset.copyToDirectory(defaultConfig);
					} catch (IOException e) {
						e.printStackTrace();
						getLogger().error("Could not unpack the default config from the jar! Maybe your Minecraft server doesn't have write permissions?");
						return;
					}
				}
			} else {
				getLogger().error("Could not find the default config file in the jar! Did you open the jar and delete it?");
				return;
			}
		}
		reloadConfig();

		CommandSpec fakeVoteCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("send a fakevote"))
				.arguments(GenericArguments.player(Text.of("player")),
						   GenericArguments.optional(GenericArguments.string(Text.of("service"))))
				.executor(new FakeVoteCommand()).build();

		CommandSpec clearqueueCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("clear Queue's database")).executor(new ClearqueueCmd()).build();

		CommandSpec cleartotalsCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("clear total's database")).executor(new CleartotalsCmd()).build();

		CommandSpec setVoteCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("set vote of player"))
				.arguments(GenericArguments.string(Text.of("player")), GenericArguments.integer(Text.of("vote")))
				.executor(new SetVoteCmd()).build();

		CommandSpec reloadCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("Reload your configs")).executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						task.cancel();
						reloadConfig();
						src.sendMessage(Text.of("Reload success"));
						return CommandResult.success();
					}
				}).build();

		CommandSpec forcequeueCmd = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("Empty the database by executing the votes")).executor(new ForcequeueCmd())
				.build();

		CommandSpec VoteCmd = CommandSpec.builder().description(Text.of("Vote Command")).executor(new VoteCommand())
				.build();

		CommandSpec votetopCmd = CommandSpec.builder().description(Text.of("Vote Top command"))
				.executor(new VoteTopCmd()).build();

		CommandSpec listenerCommandSpec = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("Plugin management"))
				// .child(VoteCmd, "vote")
				// .child(votetopCmd,"votetop")
				.child(cleartotalsCmd, "cleartotals").child(fakeVoteCmd, "fakevote").child(clearqueueCmd, "clearqueue")
				.child(forcequeueCmd, "forcequeue").child(reloadCmd, "reload").child(setVoteCmd, "set").build();

		Sponge.getCommandManager().register(this, listenerCommandSpec, "aurions");
		Sponge.getCommandManager().register(this, VoteCmd, "vote");
		Sponge.getCommandManager().register(this, votetopCmd, "votetop");
		logger.info("Main Enabled");
	}

	public static void GetSetting(ConfigurationNode Node) {
		Main.extrarandom.clear();
		Main.voteMessage.clear();
		Main.messagejoin.clear();
		Main.votetopheader.clear();
		// setting
		Main.onlineOnly = Node.getNode("settings", "onlineonly").getBoolean();
		Main.broadcastoffline = Node.getNode("settings", "broadcastoffline").getBoolean();
		Main.votecommand = Node.getNode("settings", "votecommand").getBoolean();
		Main.joinmessage = Node.getNode("settings", "joinmessage").getBoolean();
		Main.SQLType = Node.getNode("settings", "dbMode").getString();
		Main.dbHost = Node.getNode("settings", "dbHost").getString();
		Main.dbPort = Node.getNode("settings", "dbPort").getInt();
		Main.dbPrefix = Node.getNode("settings", "dbPrefix").getString();
		Main.dbUser = Node.getNode("settings", "dbUser").getString();
		Main.dbPass = Node.getNode("settings", "dbPass").getString();
		Main.dbName = Node.getNode("settings", "dbName").getString();
		Main.dbTableTotal = Node.getNode("settings", "dbTableTotal").getString();
		Main.dbTableQueue = Node.getNode("settings", "dbTableQueue").getString();
		Main.votetopnumber = Node.getNode("settings", "votetopnumber").getInt();
		Main.SQLFile = Node.getNode("settings", "dbFile").getString();
		Main.AddExtraRandom = Node.getNode("settings", "AddExtraReward").getBoolean();
		Main.GiveChanceReward = Node.getNode("settings", "GiveChanceReward").getBoolean();
		Main.delay = Node.getNode("settings", "AnnouncementDelay").getInt();
		Main.cumulativevoting = Node.getNode("settings", "cumulativevoting").getBoolean();

		// Message
		Main.voteMessage = Node.getNode("votemessage").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		Main.messagejoin = Node.getNode("joinmessage").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		Main.annoucement = Node.getNode("Announcement").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		Main.offlineBroadcast = Node.getNode("Offline", "broadcast").getString();
		Main.offlinePlayerMessage = Node.getNode("Offline", "playermessage").getString();
		// topvote
		Main.votetopformat = Node.getNode("votetopformat").getString();
		Main.votetopheader = Node.getNode("votetopheader").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
	}

	public static void GetAdvancedReward(ConfigurationNode Node) {
		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("ExtraReward").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			Main.extrarandom.add(100 - Integer.parseInt(key));
		}
		Collections.sort(Main.extrarandom);

		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("cumulativevoting").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			Main.cumulativreward.add(Integer.parseInt(key));
		}
		Collections.sort(Main.cumulativreward);

		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("perms").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			Main.permission.add(key);
		}
	}

	public void reloadConfig() {
		try {
			settingNode = settingLoader.load();
			rewardNode = rewardLoader.load();
			adrewardNode = adrewardLoader.load();
			int versionconfig = settingNode.getNode("Version").getInt();
			saveConfig();

			if (versionconfig != version || newConfig) {
				updateconfig.update(versionconfig, plugin, defaultConfig);
			}
			getLogger().info("loading successfull");
		} catch (IOException e) {
			getLogger().error("There was an error while reloading your configs");
			getLogger().error(e.toString());
		}

		GetSetting(settingNode);
		GetAdvancedReward(adrewardNode);

		if ((SQLType == "MySQL") && (dbHost.isEmpty() || dbHost == null || dbUser.isEmpty() || dbUser == null || dbPass.isEmpty() || dbPass == null)) {
			getLogger().warn("Please config database");
			Sponge.getGame().getServer().getConsole().sendMessage(
					TextSerializers.formattingCode('§').deserialize("[Main] §c----------------------"));
			Sponge.getGame().getServer().getConsole().sendMessage(
					TextSerializers.formattingCode('§').deserialize("[Main] §cPlease config database"));
			Sponge.getGame().getServer().getConsole().sendMessage(
					TextSerializers.formattingCode('§').deserialize("[Main] §c----------------------"));
		} else {
			try {
				if ((SwitchSQL.connection != null) && (!SwitchSQL.connection.isClosed())) {
					SwitchSQL.Close();
					SwitchSQL.open(Main.dbHost, Main.dbPort, Main.dbUser, Main.dbPass, Main.dbName, Main.dbPrefix);
				} else {
					SwitchSQL.open(dbHost, dbPort, dbUser, dbPass, dbName, dbPrefix);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Runnable task
		task = (Task) Task.builder().execute(new Runnable() {
			public void run() {
				for (int i = 0; i < annoucement.size(); i++) {
					MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
					messageChannel.send(Main.this.formatMessage(annoucement.get(i), "", ""));
				}
			}
		}).async().delay(delay, TimeUnit.SECONDS).interval(delay, TimeUnit.SECONDS).submit(plugin);
		if (delay < 0) {
			task.cancel();
		}
	}

    public static Main GetInstance() {
        return Main.instance;
    }


    public Text formatMessage(String message, String service, String player) {
		int votes = 0;

		if (message == null) {
			return Text.of("");
		}
		if (message.contains("<votes>")) {
			votes = SwitchSQL.TotalsVote(player);
			message = message.replace("<votes>", String.valueOf(votes));
		}
		if (message.indexOf("/") == 0) {
			message = message.substring(1);
		}
		message = message.replace("<servicename>", service).replace("<service>", service)
				.replace("<SERVICE>", service).replace("<name>", player).replace("(name)", player)
				.replace("<player>", player).replace("(player)", player).replace("<username>", player)
				.replace("(username)", player).replace("<name>", player).replace("<player>", player)
				.replace("<username>", player).replace("[name]", player).replace("[player]", player)
				.replace("[username]", player).replace("<AQUA>", "§b").replace("<BLACK>", "§0")
				.replace("<BLUE>", "§9").replace("<DARK_AQUA>", "§3").replace("<DARK_BLUE>", "§1")
				.replace("<DARK_GRAY>", "§8").replace("<DARK_GREEN>", "§2").replace("<DARK_PURPLE>", "§5")
				.replace("<DARK_RED>", "§4").replace("<GOLD>", "§6").replace("<GRAY>", "§7").replace("<GREEN>", "§a")
				.replace("<LIGHT_PURPLE>", "§d").replace("<RED>", "§c").replace("<WHITE>", "§f")
				.replace("<YELLOW>", "§e").replace("<BOLD>", "§l").replace("<ITALIC>", "§o").replace("<MAGIC>", "§k")
				.replace("<RESET>", "§r").replace("<STRIKE>", "§m").replace("<STRIKETHROUGH>", "§m")
				.replace("<UNDERLINE>", "§n").replace("<votes>", String.valueOf(votes));

		if (message.toLowerCase().contains("http")) {
			String url = "";
			Pattern pattern = Pattern.compile("http(\\S+)");
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				url = matcher.group(0);
			}
			Text text = null;
			try {
				text = TextSerializers.formattingCode('§').deserialize(message).toBuilder()
						.onClick(TextActions.openUrl(new URL(url))).build();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return Text.of("Url False, contact admin");
			}
			return text;
		}
		else {
			return TextSerializers.formattingCode('§').deserialize(message);
		}

	}

	@Listener
	public void onVote(VotifierEvent event) {
		Vote vote = event.getVote();
		String player = vote.getUsername();

		if (Main.onlineOnly) {
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			if (target.isPresent()) {
				player = target.get().getName();
				RewardsTask.online(player, vote.getServiceName());
			} else {
				SwitchSQL.offline(vote.getUsername(), vote.getServiceName(), vote.getTimeStamp(), vote.getAddress());
				Sponge.getServer().getConsole().sendMessage(Text.of("joueur pas connecter"));
			}
		} else {
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			if (target.isPresent()) {
				RewardsTask.online(player, vote.getServiceName());
			} else {
				Sponge.getServer().getConsole().sendMessage(Text.of("The player is not connected, it's impossible to give reward"));
			}
		}
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Player player = (Player) event.getTargetEntity();
		String username = player.getName();
		if ((Main.SQLType == "MySQL") && (dbHost.isEmpty() || dbHost == null || dbUser.isEmpty() || dbUser == null || dbPass.isEmpty() || dbPass == null)) {
			if (player.hasPermission("*") || player.hasPermission("listener.top")) {
				player.sendMessage(Text.builder("<Main> Please config Database.").color(TextColors.RED).build());
			}
		} else {
			if (SwitchSQL.QueueUsername(username)) {
				List<String> service = SwitchSQL.QueueReward(username);
				int totalVote = service.size();
				for (int i = 0; i < service.size(); i++) {
					RewardsTask.rewardoflline(username, service.get(i));
					SwitchSQL.removeQueue(username, service.get(i));
				}
				MessageChannel messageChannel = MessageChannel.TO_PLAYERS;

				messageChannel.send(this.formatMessage(offlineBroadcast.replace("<amt>", String.valueOf(totalVote)), "", username));
				player.sendMessage(Text.of(this.formatMessage(offlinePlayerMessage.replace("<amt>", String.valueOf(totalVote)), "", username)));
			}
			if (joinmessage) {
				for (int i = 0; i < Main.messagejoin.size(); i++) {
					player.sendMessage(formatMessage(Main.messagejoin.get(i), "", username));
				}
			}
		}
	}

	public void saveConfig() {
		try {
			settingLoader.save(settingNode);
			rewardLoader.save(rewardNode);
			adrewardLoader.save(adrewardNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
