package com.mineaurion.aurionvotelistener.sponge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.inject.Inject;
import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import com.mineaurion.aurionvotelistener.sponge.commands.CommandManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;

@Plugin(
	id = "aurionvotelistener",
	name = "AurionVoteListener",
	version = "@version@",
	authors = { "THEJean_Kevin", "Yann151924" },
	description = "A votifier listener for Sponge",
	dependencies = {
			@Dependency(id = "nuvotifier", optional = true)
	}
)
public class AurionVoteListener {

	@Inject
	@ConfigDir(sharedRoot = false)
	public Path configDir;

	//public CommandManager commandManager;
	public Task task;
	//public Config config;

	private DataSource dataSource;
    private Config config;
    private Rewards rewards;
    private AdvancedRewards advancedRewards;
    private DispatchRewards dispatchRewards;
    private Utils utils;
    private CommandManager commandManager;

	@Inject
	Game game;
	@Inject
	Logger logger;
	@Inject @DefaultConfig(sharedRoot = true)
	Path path;
	@Inject @DefaultConfig(sharedRoot = true)
	ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    public PluginContainer pluginContainer;




	@Listener
	public void preInit(GamePreInitializationEvent e){
		logger.info("AurionVoteListener loading...");

		try{
			config = new Config(configDir.toString());
			rewards = new Rewards(configDir.toString());
			advancedRewards = new AdvancedRewards(configDir.toString());
			config.init();
			rewards.init();
			advancedRewards.init();
		}
        catch (IOException exception){
			logger.error("Something went wrong with config file", exception);
			logger.error("Disabling plugin");
			disablePlugin();
		}

		if(config.version < 10){
			logger.error("Please update your config file");
			logger.error("Simply delete your config file and the plugin will generate new one");
			disablePlugin();
		}

		try{
			dataSource = new DataSource(this);
		}
        catch (SQLException exception){
			logger.error("SQL Error", exception);
			logger.error("Disabling plugin");
			disablePlugin();
		}
		utils = new Utils(this);
		dispatchRewards = new DispatchRewards(this);
	}



	@Listener
	public void onServerStart(GameStartedServerEvent event){
		loadCommands(this);
		loadTask(this);
		Sponge.getEventManager().registerListeners(this, new EventManager(this));
		logger.info("AurionsVoteListener Enabled");
	}

	//TODO : Check if it's work
	@Listener
	public void onServerReload(GameReloadEvent event) throws IOException{
		task.cancel();
		loadTask(this);

		config = new Config(configDir.toString());
		rewards = new Rewards(configDir.toString());
		advancedRewards = new AdvancedRewards(configDir.toString());

		event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole()).sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&6Reload Finish"));
	}

    private void disablePlugin(){
        Sponge.getEventManager().unregisterListeners(this);
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

	private void loadTask(AurionVoteListener main) {
	    int delay = config.announcement.delay;
		task = Task.builder().execute(new Runnable() {
			public void run() {
                for (String announce: config.announcement.message) {
                    MessageChannel.TO_PLAYERS.send(TextSerializers.FORMATTING_CODE.deserialize(announce));
                }
			}
		})
		.async()
		.delay(delay, TimeUnit.SECONDS)
		.interval(delay, TimeUnit.SECONDS)
		.submit(main);

		if (delay < 0) {
			task.cancel();
		}

	}


	private void loadCommands(AurionVoteListener plugin) {
	    commandManager = new CommandManager(plugin);
		Sponge.getCommandManager().register(this, commandManager.listenerCommand, "aurions");
		Sponge.getCommandManager().register(this, commandManager.voteCommand, "vote");
		Sponge.getCommandManager().register(this, commandManager.voteTopCommand, "votetop");
	}


	public Logger getLogger(){
		return logger;
	}

    public Config getConfig(){
        return this.config;
    }

    public Rewards getRewards(){
        return this.rewards;
    }

    public AdvancedRewards getAdvancedRewards(){
        return this.advancedRewards;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }

    public DispatchRewards getDispatchRewards(){
	    return this.dispatchRewards;
    }

    public Utils getUtils(){
		return this.utils;
	}

    public void sendConsoleMessage(String message){
	    Sponge.getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));
    }

}
