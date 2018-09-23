package com.mineaurion.aurionvotelistener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.inject.Inject;
import com.mineaurion.aurionvotelistener.SQL.MySqlTask;
import com.mineaurion.aurionvotelistener.SQL.SQLTask;
import com.mineaurion.aurionvotelistener.SQL.SwitchSQL;
import com.mineaurion.aurionvotelistener.commands.CommandManager;

@Plugin(
		id = "aurionsvotelistener",
		name = "AurionsVoteListener",
		authors = { "THEJean_Kevin","Yann151924" },
		description = "A votifier listener for Sponge",
		dependencies = {
				@Dependency(id = "nuvotifier", optional = true)
		})

public class Main {
	@Inject
	private Game game;
	
	@Inject
    public PluginContainer pluginContainer;
	
	@Inject
	public Logger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	public Path configDir;

	public CommandManager commandManager;

	


	// global
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	public Task task;
	
	public SwitchSQL switchsql;
	public MySqlTask mysqltask;
	public SQLTask sqltask;
	public Rewards rewardTask;
	public Config config;
	
	@Listener
	public void onInitialization(GameInitializationEvent event) throws IOException {
		logger.info("AurionsVoteListener Vote loading...");
		logger.info("Trying To setup Config Loader");
		
		config = new Config(this);
		Sponge.getEventManager().registerListeners(this, new EventManager(this));

	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) throws IOException, SQLException {
		loadCommands(this);
		switchsql = new SwitchSQL(this, game);
		mysqltask = new MySqlTask();
		sqltask = new SQLTask();
		initialiseDatabase();
		rewardTask = new Rewards(this);
		
		loadTask(this);
		logger.info("AurionsVoteListener Enabled");
	}
	
	@Listener
	public void onServerReload(GameReloadEvent event) throws IOException, SQLException {
		task.cancel();
		config = new Config(this);
		initialiseDatabase();
		loadTask(this);
		MessageReceiver src = event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole());
		src.sendMessage(Text.of(formatmessage("<YELLOW>Reload Finish", "", "")));
	}
	
	
	private void loadTask(Main main) {
		task = Task.builder().execute(new Runnable() {
			public void run() {
				for (int i = 0; i < Config.annoucement.size(); i++) {
					MessageChannel messageChannel = MessageChannel.TO_PLAYERS;
					messageChannel.send(formatmessage(Config.annoucement.get(i), "", ""));
				}
			}
		})
		.async()
		.delay(Config.delay, TimeUnit.SECONDS)
		.interval(Config.delay, TimeUnit.SECONDS)
		.submit(main);
		
		if (Config.delay < 0) {
			task.cancel();
		}
		
	}

	private void initialiseDatabase() throws SQLException {
		sendmessage("<YELLOW>Loading DataBase", "console");
		if ((Config.SQLType == "MySQL") && (Config.dbHost.isEmpty() || Config.dbHost == null || Config.dbUser.isEmpty() || Config.dbUser == null || Config.dbPass.isEmpty() || Config.dbPass == null)) {
			logger.warn("Please config database");
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize("[AurionsVoteListener] &c----------------------"));
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize("[AurionsVoteListener] &cPlease config database"));
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize("[AurionsVoteListener] &c----------------------"));
		}else {
			if ((SwitchSQL.datasource != null) && (!SwitchSQL.datasource.getConnection().isClosed())) {
				SwitchSQL.datasource.getConnection().close();
			}
			switchsql.open();
		}
	}

	private void loadCommands(Main main) {
		commandManager = new CommandManager(main);
		Sponge.getCommandManager().register(this, commandManager.listenerCommandSpec, "aurions");
		Sponge.getCommandManager().register(this, commandManager.voteCmd, "vote");
		Sponge.getCommandManager().register(this, commandManager.votetopCmd, "votetop");
	}

	public void sendmessage(String message, String sender) {
		if (sender.equals("console")||sender.equals("Server")) {
			Sponge.getGame().getServer().getConsole().sendMessage(formatmessage(message, "", sender));
		} else {
			Sponge.getGame().getServer().getPlayer(sender).get().sendMessage(formatmessage(message, "", sender));
		}
	}
	
	public Text formatmessage(String message, String service, String player) {
		if (message == null) {
			return Text.of("");
		}
		String serviceName = service;
		String playerName = player;
		int votes = 0;

		if (message.contains("<votes>")) {
			votes = switchsql.TotalsVote(playerName);
			message = message.replace("<votes>", String.valueOf(votes));
		}

		if (message.indexOf("/") == 0) {
			message = message.substring(1);
		}
		message = message
				.replace("<servicename>", serviceName).replace("<service>", serviceName).replace("<SERVICE>", serviceName)
				.replace("<name>", playerName).replace("(name)", playerName)
				.replace("<player>", playerName).replace("(player)", playerName).replace("<username>", playerName)
				.replace("(username)", playerName).replace("<name>", playerName).replace("<player>", playerName)
				.replace("<username>", playerName).replace("[name]", playerName).replace("[player]", playerName).replace("[username]", playerName)
				.replace("<AQUA>", "&b")
				.replace("<BLACK>", "&0")
				.replace("<BLUE>", "&9")
				.replace("<DARK_AQUA>", "&3")
				.replace("<DARK_BLUE>", "&1")
				.replace("<DARK_GRAY>", "&8")
				.replace("<DARK_GREEN>", "&2")
				.replace("<DARK_PURPLE>", "&5")
				.replace("<DARK_RED>", "&4")
				.replace("<GOLD>", "&6")
				.replace("<GRAY>", "&7")
				.replace("<GREEN>", "&a")
				.replace("<LIGHT_PURPLE>", "&d")
				.replace("<RED>", "&c")
				.replace("<WHITE>", "&f")
				.replace("<YELLOW>", "&e")
				.replace("<BOLD>", "&l")
				.replace("<ITALIC>", "&o")
				.replace("<MAGIC>", "&k")
				.replace("<RESET>", "&r")
				.replace("<STRIKE>", "&m")
				.replace("<STRIKETHROUGH>", "&m")
				.replace("<UNDERLINE>", "&n")
				.replace("<votes>", String.valueOf(votes));

		if (message.toLowerCase().contains("http")) {
			String url = "";
			Pattern pattern = Pattern.compile("http(\\S+)");
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				url = matcher.group(0);
			}
			Text text = null;
			try {
				text = TextSerializers.FORMATTING_CODE.deserialize(message).toBuilder()
						.onClick(TextActions.openUrl(new URL(url))).build();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return Text.of("Url False, contact admin");
			}

			return text;
		}

		else {
			return TextSerializers.FORMATTING_CODE.deserialize(message);
		}

	}

}
