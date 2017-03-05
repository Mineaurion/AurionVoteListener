package com.mineaurion.tjk.AurionsVoteListener;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;

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
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.inject.Inject;
import com.mineaurion.tjk.AurionsVoteListener.commands.ClearqueueCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.CleartotalsCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.FakeVoteCommand;
import com.mineaurion.tjk.AurionsVoteListener.commands.ForcequeueCmd;
import com.mineaurion.tjk.AurionsVoteListener.commands.VoteCommand;
import com.mineaurion.tjk.AurionsVoteListener.commands.VoteTopCmd;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id =AurionsVoteListener.AURIONS_ID, name="AurionsVoteListener",version="1.0",authors = {"THEJean_Kevin"}, description = "A votifier listener for Sponge", dependencies = {@Dependency(id = "nuvotifier", optional = true)})
public class AurionsVoteListener {
	
	@Inject Logger logger;
    public Logger getLogger()
    {
        return logger;
    }
    
    @Inject private PluginContainer plugin;
    
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
	
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;
    public Path ConfigDir;
    private CommentedConfigurationNode rootNode;
    public CommentedConfigurationNode getNode(){
		return this.rootNode;
	}
    //global
	private static AurionsVoteListener instance;
	public String SQLType;
	public String SQLFile;
	public final static String AURIONS_ID = "aurionsvotelistener";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	//Settings
	public boolean onlineOnly=true;
	public boolean broadcastoffline=false;
	public boolean votecommand=true;
	public boolean joinmessage=true;
	public String dbHost;
	public int dbPort=3306;
	public String dbUser;
	public String dbPass;
	public String dbName;
	public String dbPrefix;
	public String dbTableTotal = "ListenerTotal";
	public String dbTableQueue = "ListenerQueue";
	public int votetopnumber=10;
	public boolean AddExtraRandom=false;
	public boolean GiveChanceReward=true;
	public static List<Integer> extrarandom = new ArrayList<Integer>();
	public int delay = 300;
	
	//Message
	public static List<String> voteMessage = new ArrayList<String>();
	public List<String> messagejoin = new ArrayList<String>();
	public List<String> annoucement = new ArrayList<String>();
	
	//votetop
	public String votetopformat="<POSITION>. <GREEN><username> - <WHITE><TOTAL>";
	public static List<String> votetopheader = new ArrayList<String>();
	
	

	
	@Listener
	public void onInitialization(GameInitializationEvent event) 
	 {
		AurionsVoteListener.instance = this;
		ConfigDir = privateConfigDir;
		getLogger().info("AurionsVoteListener Vote loading...");
        getLogger().info("Trying To setup Config Loader");
		
        Asset configAsset = plugin.getAsset("aurionsvotelistener.conf").get();


        if (Files.notExists(defaultConfig)) {
            if (configAsset != null) {
                try {
                    getLogger().info("Copying Default Config");
                    configAsset.copyToFile(defaultConfig);
                } catch (IOException e) {
                    e.printStackTrace();
                    getLogger().error("Could not unpack the default config from the jar! Maybe your Minecraft server doesn't have write permissions?");
                    return;
                }
            } else {
                getLogger().error("Could not find the default config file in the jar! Did you open the jar and delete it?");
                return;
            }
        }
        
         
        reloadConfig();
        
        
        
		CommandSpec fakeVoteCmd = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("send a fakevote"))
				 .arguments(
				GenericArguments.player(Text.of("player")),
				GenericArguments.optional(GenericArguments.string(Text.of("service")))
				)
				 .executor(new FakeVoteCommand())
				 .build();
	
		CommandSpec clearqueueCmd = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("clear Queue's database"))
				 .executor(new ClearqueueCmd())
				 .build();
		 
		CommandSpec cleartotalsCmd = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("clear total's database"))
				 .executor(new CleartotalsCmd())
				 .build();
		
		CommandSpec reloadCmd = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("Reload your configs"))
				 .executor(new CommandExecutor(){

					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						reloadConfig();
						src.sendMessage(Text.of("Reload success"));
						return CommandResult.success();
					}
					 
				 })
				 .build();
		 
		 CommandSpec forcequeueCmd = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("Empty the database by executing the votes"))
				 .executor(new ForcequeueCmd())
				 .build();
		 
		 CommandSpec VoteCmd = CommandSpec.builder()
				 .description(Text.of("Vote Command"))
				 .executor(new VoteCommand())
				 .build();
		 
		 CommandSpec votetopCmd = CommandSpec.builder()
				 .description(Text.of("Vote Top command"))
				 .executor(new VoteTopCmd())
				 .build();
		 
		 CommandSpec listenerCommandSpec = CommandSpec.builder()
				 .permission("listener.admin")
				 .description(Text.of("Plugin management"))
				 //.child(VoteCmd, "vote")
				 //.child(votetopCmd,"votetop")
				 .child(cleartotalsCmd,"cleartotals")
				 .child(fakeVoteCmd, "fakevote")
				 .child(clearqueueCmd,"clearqueue")
				 .child(forcequeueCmd,"forcequeue")
				 .child(reloadCmd, "reload")
				 .build();
		 
		 Sponge.getCommandManager().register(this, listenerCommandSpec, "Aurions");
		 Sponge.getCommandManager().register(this, VoteCmd, "Vote");
		 Sponge.getCommandManager().register(this, votetopCmd, "Votetop");
		 logger.info("AurionsVoteListener Enabled");
		 
		 Task task = (Task) Task.builder().execute(new Runnable() {
			 public void run(){
				 for(int i = 0;i<annoucement.size();i++){
				 	MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
					messageChannel.send(AurionsVoteListener.GetInstance().formatmessage(annoucement.get(i), "", ""));
				 }
			 }
		 }).async().delayTicks(delay*20).intervalTicks(delay*20).submit(plugin);
		 if(delay<0){task.cancel();}
	 }
	
	
	
	
	
	public void GetValues(ConfigurationNode Node){
		extrarandom.clear();
		voteMessage.clear();
		messagejoin.clear();
		votetopheader.clear();
		//seting
		onlineOnly = Node.getNode("settings","onlineonly").getBoolean();
		broadcastoffline = Node.getNode("settings","broadcastoffline").getBoolean();
		votecommand = Node.getNode("settings","votecommand").getBoolean();
		joinmessage = Node.getNode("settings","joinmessage").getBoolean();
		dbHost= Node.getNode("settings","dbHost").getString();
		dbPort= Node.getNode("settings","dbPort").getInt();
		dbUser = Node.getNode("settings","dbUser").getString();
		dbPass = Node.getNode("settings","dbPass").getString();
		dbName = Node.getNode("settings","dbName").getString();
		dbPrefix = Node.getNode("settings","dbPrefix").getString();
		dbTableTotal = Node.getNode("settings","dbTableTotal").getString();
		dbTableQueue = Node.getNode("settings","dbTableQueue").getString();
		votetopnumber = Node.getNode("settings","votetopnumber").getInt();
		SQLFile = Node.getNode("settings","dbFile").getString();
		SQLType = Node.getNode("settings","dbMode").getString();
		AddExtraRandom = Node.getNode("settings","AddExtraRandom").getBoolean();
		GiveChanceReward = Node.getNode("settings","GiveChanceReward").getBoolean();
		delay = Node.getNode("settings","AnnouncementDelay").getInt();
		
		for(Entry<Object, ? extends ConfigurationNode> markers : rootNode.getNode("ExtraReward").getChildrenMap().entrySet())
		{
			String key = (String) markers.getKey();
			extrarandom.add(100-Integer.parseInt(key));
		}
		Collections.sort(extrarandom);
		
		
		
		//Message
		voteMessage = Node.getNode("votemessage").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		messagejoin = Node.getNode("joinmessage").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		annoucement = Node.getNode("Announcement").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

		//topvote
		votetopformat = Node.getNode("votetopformat").getString();
		votetopheader = Node.getNode("votetopheader").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		}
	
	
	public void reloadConfig(){
		try {
            rootNode = loader.load();
            getLogger().info("loading successfull");
        } catch (IOException e) {
            getLogger().error("There was an error while reloading your configs");
            getLogger().error(e.toString());
        }
		
		GetValues(rootNode);
		if((SQLType=="MySQL")&&(dbHost.isEmpty()||dbHost==null||dbUser.isEmpty()||dbUser==null||dbPass.isEmpty()||dbPass==null)){
			getLogger().warn("Please config database");
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.formattingCode('§').deserialize("[AurionsVoteListener] §c----------------------"));
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.formattingCode('§').deserialize("[AurionsVoteListener] §cPlease config database"));
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.formattingCode('§').deserialize("[AurionsVoteListener] §c----------------------"));
		}else{
			try {
				if ((SwitchSQL.connection != null)&& (!SwitchSQL.connection.isClosed())){
				SwitchSQL.Close();
				SwitchSQL.open(AurionsVoteListener.GetInstance().dbHost,AurionsVoteListener.GetInstance().dbPort,AurionsVoteListener.GetInstance().dbUser,AurionsVoteListener.GetInstance().dbPass,AurionsVoteListener.GetInstance().dbName,AurionsVoteListener.GetInstance().dbPrefix);
				}else{
					SwitchSQL.open(dbHost,dbPort,dbUser,dbPass,dbName,dbPrefix);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	


	public static AurionsVoteListener GetInstance(){
		return AurionsVoteListener.instance;
	}
	
	
	public Text formatmessage(String message,String service,String player) {
		if (message == null){
		return Text.of("");
		}
		String serviceName = service;
		String playerName = player;
	    int votes = 0;
	    
	    
	    if (message.contains("<votes>")){
			votes = SwitchSQL.TotalsVote(playerName);
			message = message.replace("<votes>", String.valueOf(votes));
			}
	    
	    
	    
	    if (message.indexOf("/") == 0) {
	        message = message.substring(1);
	      }
	    message = message.replace("<servicename>", serviceName).replace("<service>", serviceName).replace("<SERVICE>", serviceName).replace("<name>", playerName).replace("(name)", playerName)
	    	      .replace("<player>", playerName).replace("(player)", playerName).replace("<username>", playerName).replace("(username)", playerName).replace("<name>", playerName)
	    	      .replace("<player>", playerName).replace("<username>", playerName).replace("[name]", playerName).replace("[player]", playerName).replace("[username]", playerName)
	    	      .replace("<AQUA>", "§b").replace("<BLACK>", "§0").replace("<BLUE>", "§9")
	    	      .replace("<DARK_AQUA>", "§3").replace("<DARK_BLUE>", "§1").replace("<DARK_GRAY>", "§8")
	    	      .replace("<DARK_GREEN>", "§2").replace("<DARK_PURPLE>", "§5").replace("<DARK_RED>", "§4")
	    	      .replace("<GOLD>", "§6").replace("<GRAY>", "§7").replace("<GREEN>", "§a")
	    	      .replace("<LIGHT_PURPLE>", "§d").replace("<RED>", "§c").replace("<WHITE>", "§f")
	    	      .replace("<YELLOW>", "§e").replace("<BOLD>", "§l").replace("<ITALIC>", "§o")
	    	      .replace("<MAGIC>", "§k").replace("<RESET>", "§r").replace("<STRIKE>", "§m")
	    	      .replace("<STRIKETHROUGH>", "§m").replace("<UNDERLINE>", "§n").replace("<votes>", String.valueOf(votes));
	    return TextSerializers.formattingCode('§').deserialize(message);
	    
	}
	
	@Listener
	public void onVote(VotifierEvent event){
		Vote vote = event.getVote();
		String player = vote.getUsername();
		
		if(AurionsVoteListener.GetInstance().onlineOnly){
			Optional<Player> target = Sponge.getServer().getPlayer(player);
			if(target.isPresent()){
				player = target.get().getName();
				RewardsTask.online(player,vote.getServiceName());
			}else{
				SwitchSQL.offline(vote.getUsername(),vote.getServiceName(),vote.getTimeStamp(),vote.getAddress());
				Sponge.getServer().getConsole().sendMessage(Text.of("joueur pas connecter"));	
			}
		}
		else{
			RewardsTask.online(player, vote.getServiceName());
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event)
	{
		Player player = (Player)event.getTargetEntity();
		String username = player.getName();
		if((SQLType=="MySQL")&&dbHost.isEmpty()||dbHost==null||dbUser.isEmpty()||dbUser==null||dbPass.isEmpty()||dbPass==null){
			if(player.hasPermission("*")||player.hasPermission("listener.top")){
				player.sendMessage(Text.builder("<AurionsVoteListener> Please config Database.").color(TextColors.RED).build());
			}
		}
		else{
			if(SwitchSQL.QueueUsername(username)){
				
				List<String> service = SwitchSQL.QueueReward(username);
				 for(int i = 0; i < service.size(); i++)
				    {
					 RewardsTask.online(username, service.get(i));
					 SwitchSQL.removeQueue(username, service.get(i));
				    }
			}else{
			}
			if(joinmessage){
				for(int i = 0; i<AurionsVoteListener.GetInstance().messagejoin.size();i++){
					player.sendMessage(formatmessage(AurionsVoteListener.GetInstance().messagejoin.get(i),"",username));
				}
			}
		}
	}
}
