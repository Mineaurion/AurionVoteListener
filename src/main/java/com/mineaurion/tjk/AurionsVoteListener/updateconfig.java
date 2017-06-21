package com.mineaurion.tjk.AurionsVoteListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.plugin.PluginContainer;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class updateconfig {
	static PluginContainer plugin;
	static Path defaultConfig;
	//version actuelle 6
	public static void update(int version, PluginContainer plugin, Path defaultConfig, ConfigurationLoader<CommentedConfigurationNode> loader) throws IOException {
		updateconfig.plugin = plugin;
		updateconfig.defaultConfig = defaultConfig;

		if (version <= 5) {
			version6(loader);
			version7(loader);
		}
		if(version<=6){
			version7(loader);
		}
	}
	
	public static void version7(ConfigurationLoader<CommentedConfigurationNode> loader) throws IOException {
		Asset v7 = plugin.getAsset("Version7.conf").get();
		File fin = new File(defaultConfig.toString());
		FileWriter fstream = new FileWriter(fin, true);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write(v7.readString());
		out.close();
		
		CommentedConfigurationNode rootNode = loader.load();
		
		rootNode.getNode("Version").setValue(7);
		
		loader.save(rootNode);
	}

	public static void version6(ConfigurationLoader<CommentedConfigurationNode> loader) throws IOException {

		Asset v6 = plugin.getAsset("Version6.conf").get();
		File fin = new File(defaultConfig.toString());
		FileWriter fstream = new FileWriter(fin, true);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write(v6.readString());
		out.close();
		
		CommentedConfigurationNode rootNode = loader.load();
		AurionsVoteListener.GetInstance().old = true;
		
		rootNode.getNode("Version").setValue(6);
		rootNode.getNode("settings","cumulativevoting").setValue(false);
		
		loader.save(rootNode);
		

	}
}
