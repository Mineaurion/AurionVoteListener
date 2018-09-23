package com.mineaurion.aurionvotelistener;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spongepowered.api.asset.Asset;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Config {
	
	private Main plugin;
	
	private Path settingPath;
	public static ConfigurationLoader<CommentedConfigurationNode> settingLoader;
	public CommentedConfigurationNode settingNode;

	private Path rewardPath;
	public static ConfigurationLoader<CommentedConfigurationNode> rewardLoader;
	public CommentedConfigurationNode rewardNode;

	private Path adrewardPath;
	public static ConfigurationLoader<CommentedConfigurationNode> adrewardLoader;
	public CommentedConfigurationNode adrewardNode;
	
	public Config(Main main) throws IOException {
		plugin = main;
		settingPath = Paths.get(plugin.configDir + "/Setting.conf");
		rewardPath = Paths.get(plugin.configDir + "/Reward.conf");
		adrewardPath = Paths.get(plugin.configDir + "/AdvancedReward.conf");
		
		
		
		configCheck();
	}

	private void configCheck() throws IOException {
		if(!Files.exists(settingPath) && !Files.exists(rewardPath) && !Files.exists(adrewardPath)) {
			plugin.sendmessage("<YELLOW>Copy config File", "console");
			
			Asset configAsset = plugin.pluginContainer.getAsset("Setting.conf").get();
			Asset RewardAsset = plugin.pluginContainer.getAsset("Reward.conf").get();
			Asset AdRewardAsset = plugin.pluginContainer.getAsset("AdvancedReward.conf").get();
		
			if(!plugin.configDir.toFile().exists()) {
				Files.createDirectories(plugin.configDir);
			}
			configAsset.copyToDirectory(plugin.configDir);
			RewardAsset.copyToDirectory(plugin.configDir);
			AdRewardAsset.copyToDirectory(plugin.configDir);
		}	
		
		settingLoader = HoconConfigurationLoader.builder().setPath(settingPath).build();
		rewardLoader = HoconConfigurationLoader.builder().setPath(rewardPath).build();
		adrewardLoader = HoconConfigurationLoader.builder().setPath(adrewardPath).build();
	
		settingNode = settingLoader.load();
		rewardNode = rewardLoader.load();
		adrewardNode = adrewardLoader.load();
		
		getSetting(settingNode);
		GetAdvancedReward(adrewardNode);
	}
	
	private void getSetting(ConfigurationNode Node) {
		extrarandom.clear();
		voteMessage.clear();
		messagejoin.clear();
		votetopheader.clear();
		
		onlineOnly = Node.getNode("settings", "onlineonly").getBoolean();
		broadcastoffline = Node.getNode("settings", "broadcastoffline").getBoolean();
		votecommand = Node.getNode("settings", "votecommand").getBoolean();
		joinmessage = Node.getNode("settings", "joinmessage").getBoolean();
		SQLType = Node.getNode("settings", "dbMode").getString();
		dbHost = Node.getNode("settings", "dbHost").getString();
		dbPort = Node.getNode("settings", "dbPort").getInt();
		dbPrefix = Node.getNode("settings", "dbPrefix").getString();
		dbUser = Node.getNode("settings", "dbUser").getString();
		dbPass = Node.getNode("settings", "dbPass").getString();
		dbName = Node.getNode("settings", "dbName").getString();
		dbTableTotal = Node.getNode("settings", "dbTableTotal").getString();
		dbTableQueue = Node.getNode("settings", "dbTableQueue").getString();
		votetopnumber = Node.getNode("settings", "votetopnumber").getInt();
		SQLFile = Node.getNode("settings", "dbFile").getString();
		AddExtraRandom = Node.getNode("settings", "AddExtraReward").getBoolean();
		GiveChanceReward = Node.getNode("settings", "GiveChanceReward").getBoolean();
		delay = Node.getNode("settings", "AnnouncementDelay").getInt();
		cumulativevoting = Node.getNode("settings", "cumulativevoting").getBoolean();

		// Message
		voteMessage = Node.getNode("votemessage").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		messagejoin = Node.getNode("joinmessage").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		annoucement = Node.getNode("Announcement").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());
		offlineBroadcast = Node.getNode("Offline", "broadcast").getString();
		offlinePlayerMessage = Node.getNode("Offline", "playermessage").getString();
		// topvote
		votetopformat = Node.getNode("votetopformat").getString();
		votetopheader = Node.getNode("votetopheader").getChildrenList().stream()
				.map(ConfigurationNode::getString).collect(Collectors.toList());

	}
	
	public static void GetAdvancedReward(ConfigurationNode Node) {

		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("ExtraReward").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			extrarandom.add(key);
		}
		Collections.sort(extrarandom,Collections.reverseOrder());

		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("cumulativevoting").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			cumulativreward.add(Integer.parseInt(key));
		}
		Collections.sort(cumulativreward);

		for (Entry<Object, ? extends ConfigurationNode> markers : Node.getNode("perms").getChildrenMap().entrySet()) {
			String key = (String) markers.getKey();
			permission.add(key);
		}
		
		
	}

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
		public static List<String> extrarandom = new ArrayList<String>();
		public static List<Integer> cumulativreward = new ArrayList<Integer>();
		public static List<String> permission = new ArrayList<String>();
		public static int delay = 300;
		public static boolean cumulativevoting = false;
		
		public static String SQLType;
		public static String SQLFile;
		
	// Message
		public static List<String> voteMessage = new ArrayList<String>();
		public static List<String> messagejoin = new ArrayList<String>();
		public static List<String> annoucement = new ArrayList<String>();
		public static String offlineBroadcast;
		public static String offlinePlayerMessage;

	// votetop
		public static String votetopformat = "<POSITION>. <GREEN><username> - <WHITE><TOTAL>";
		public static List<String> votetopheader = new ArrayList<String>();
}
