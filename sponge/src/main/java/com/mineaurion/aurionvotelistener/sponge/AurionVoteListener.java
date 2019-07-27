package com.mineaurion.aurionvotelistener.sponge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;



import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;
import com.mineaurion.aurionvotelistener.sponge.database.DataSource;
import com.mineaurion.aurionvotelistener.sponge.commands.CommandManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
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
	private Task task;

	private DataSource dataSource;
    private Config config;
    private Rewards rewards;
    private AdvancedRewards advancedRewards;
    private DispatchRewards dispatchRewards;
    private Utils utils;

	@Inject
	Logger logger;
	@Inject
	@ConfigDir(sharedRoot = false)
	public Path configDir;
    @Inject
    public PluginContainer pluginContainer;

	@Listener
	public void preInit(GamePreInitializationEvent e){
		logger.info("AurionVoteListener loading...");

		try{
			if(Files.notExists(Paths.get(configDir + "/config.conf"))){
				pluginContainer.getAsset("config.conf").get().copyToDirectory(configDir);
			}
			if(Files.notExists(Paths.get(configDir + "/reward.conf"))){
				pluginContainer.getAsset("reward.conf").get().copyToDirectory(configDir);
			}

			if(Files.notExists(Paths.get(configDir + "/advanced-reward.conf"))){
				pluginContainer.getAsset("advanced-reward.conf").get().copyToDirectory(configDir);
			}
			loadConfig();
		}
        catch (IOException exception){
			logger.error("[AurionsVoteListener] Something went wrong with config file", exception);
			logger.error("[AurionsVoteListener] Disabling plugin");
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

	@Listener
	public void onServerReload(GameReloadEvent event){
		task.cancel();
		loadConfig();
		loadTask(this);
		event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole()).sendMessage(TextSerializers.FORMATTING_CODE.deserialize("[AurionsVoteListener] &6Reload Finish"));
	}

    private void disablePlugin(){
        Sponge.getEventManager().unregisterListeners(this);
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

	private void loadTask(AurionVoteListener main) {
	    int delay = config.settings.announcement.delay;
		task = Task.builder().execute(new Runnable() {
			public void run() {
                for (String announce: config.settings.announcement.message) {
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
	    CommandManager commandManager = new CommandManager(plugin);
		Sponge.getCommandManager().register(this, commandManager.listenerCommand, "aurion");
		Sponge.getCommandManager().register(this, commandManager.voteCommand, "vote");
		Sponge.getCommandManager().register(this, commandManager.voteTopCommand, "votetop");
	}

	private void loadConfig(){
		ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configDir + File.separator +  "config.conf")).build();
		ConfigurationLoader<CommentedConfigurationNode> rewardLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configDir + File.separator +  "reward.conf")).build();
		ConfigurationLoader<CommentedConfigurationNode> advancedRewardLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configDir + File.separator +  "advanced-reward.conf")).build();
		try{
			this.config = configLoader.load().getValue(TypeToken.of(Config.class));
			this.rewards = rewardLoader.load().getValue(TypeToken.of(Rewards.class));
			this.advancedRewards = advancedRewardLoader.load().getValue(TypeToken.of(AdvancedRewards.class));
		}
		catch (IOException | ObjectMappingException exception){
			logger.error("Error loading file", exception);
		}
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
