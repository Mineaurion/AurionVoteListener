package com.mineaurion.aurionvotelistener.commands;


import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionvotelistener.Main;




public class CommandManager {
	private Main plugin;
	private CommandSpec fakeVoteCmd;
	private CommandSpec clearqueueCmd;
	private CommandSpec cleartotalsCmd;
	private CommandSpec setVoteCmd;
	private CommandSpec forcequeueCmd;
	
	public CommandSpec voteCmd;
	public CommandSpec votetopCmd;
	public CommandSpec listenerCommandSpec;
	
	public CommandManager(Main main) {
		plugin = main;
		Init();
	}

	public void Init() {
		fakeVoteCmd = CommandSpec.builder()
				.permission("listener.admin")
				.description(Text.of("send a fakevote"))
				.arguments(GenericArguments.player(Text.of("player")),GenericArguments.optional(GenericArguments.string(Text.of("service"))))
				.executor(new FakeVoteCommand(plugin))
				.build();
		 clearqueueCmd = CommandSpec.builder()
				.permission("listener.admin")
				.description(Text.of("clear Queue's database"))
				.executor(new ClearqueueCmd(plugin))
				.build();

		 cleartotalsCmd = CommandSpec.builder()
				.permission("listener.admin")
				.description(Text.of("clear total's database"))
				.executor(new CleartotalsCmd(plugin))
				.build();

		 setVoteCmd = CommandSpec.builder()
				.permission("listener.admin")
				.description(Text.of("set vote of player"))
				.arguments(GenericArguments.string(Text.of("player")), GenericArguments.integer(Text.of("vote")))
				.executor(new SetVoteCmd(plugin))
				.build();
		 
		forcequeueCmd = CommandSpec.builder().permission("listener.admin")
					.description(Text.of("Empty the database by executing the votes"))
					.executor(new ForcequeueCmd(plugin))
					.build();

		voteCmd = CommandSpec.builder()
				.description(Text.of("Vote Command"))
				.executor(new VoteCommand(plugin))
				.build();

		votetopCmd = CommandSpec.builder()
				.description(Text.of("Vote Top command"))
				.executor(new VoteTopCmd(plugin))
				.build();
		
		listenerCommandSpec = CommandSpec.builder().permission("listener.admin")
				.description(Text.of("Plugin management"))
				.child(cleartotalsCmd, "cleartotals")
				.child(fakeVoteCmd, "fakevote")
				.child(clearqueueCmd, "clearqueue")
				.child(forcequeueCmd, "forcequeue")
				.child(setVoteCmd, "set").build();
	}

}
