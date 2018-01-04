package com.mineaurion.tjk.AurionsVoteListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.spongepowered.api.plugin.PluginContainer;

public class updateconfig {
	static PluginContainer plugin;
	static Path defaultConfig;

	// version actuelle 8
	public void update(int version, PluginContainer plugin, Path defaultConfig) throws IOException {
		updateconfig.plugin = plugin;
		updateconfig.defaultConfig = defaultConfig;

		version9();

		// Not supported
		/*
		 * if (version <= 5) { version6(loader); version7(loader); version8(loader); }
		 * if(version<=6){ version7(loader); version8(loader); } if(version<=7){
		 * version8(loader); }
		 */
	}

	public void version9() {

		try {
			setValue();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Paths.get(defaultConfig + "/aurionsvotelistener.conf").toFile()
				.renameTo(Paths.get(defaultConfig + "/aurionsvotelistener.old").toFile());

	}

	private void setValue() throws IOException {
		File old = Paths.get(defaultConfig + "/aurionsvotelistener.conf").toFile();
		FileInputStream fis = new FileInputStream(old);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));

		File outF = new File(defaultConfig + "/Setting.conf");
		FileWriter fstream = new FileWriter(outF, true);
		BufferedWriter out = new BufferedWriter(fstream);

		String aLine = null;
		Boolean copy = false;
		Boolean copyafter = false;
		while ((aLine = in.readLine()) != null) {
			if (aLine.startsWith("Version"))
				copyafter = true;
			if (aLine.startsWith("settings"))
				copy = true;
			if (copy && aLine.contains("}")) {
				copyafter = true;
				copy = false;
			}
			if (aLine.startsWith("Offline"))
				copy = true;
			if (copy && aLine.contains("}")) {
				copyafter = true;
				copy = false;
			}
			if (aLine.startsWith("votemessage"))
				copy = true;
			if (copy && aLine.contains("]")) {
				copyafter = true;
				copy = false;
			}
			if (aLine.startsWith("joinmessage"))
				copy = true;
			if (copy && aLine.contains("]")) {
				copyafter = true;
				copy = false;
			}
			if (aLine.startsWith("Announcement"))
				copy = true;
			if (copy && aLine.contains("]")) {
				copyafter = true;
				copy = false;
			}
			if (aLine.startsWith("votetopformat"))
				copyafter = true;
			if (aLine.startsWith("votetopheader"))
				copy = true;
			if (copy && aLine.contains("]")) {
				copyafter = true;
				copy = false;
			}

			if (copy) {
				out.write(aLine);
				out.newLine();
			}
			if (copyafter) {
				out.write(aLine);
				out.newLine();
				copyafter = false;
			}
		}
		
		out.close();
		in.close();
		
		File old1 = Paths.get(defaultConfig + "/aurionsvotelistener.conf").toFile();
		FileInputStream fis1 = new FileInputStream(old1);
		BufferedReader in1 = new BufferedReader(new InputStreamReader(fis1));
		
		File outF1 = new File(defaultConfig + "/Reward.conf");
		FileWriter fstream1 = new FileWriter(outF1, true);
		BufferedWriter out1 = new BufferedWriter(fstream1);
		
		
		int count = 0;
		while ((aLine = in1.readLine()) != null) {
			if (aLine.startsWith("services"))
				copy = true;
			if (copy && aLine.contains("{")) {
				count += 1;
			}
			if (copy && aLine.contains("}")) {
				count -= 1;
			}
			if (copy && count == 0) {
				copyafter = true;
				copy = false;
			}
			if (copy) {
				out1.write(aLine);
				out1.newLine();
			}
			if (copyafter) {
				out1.write(aLine);
				out1.newLine();
				copyafter = false;
			}
		}

		out1.close();
		in1.close();
		
		File old2 = Paths.get(defaultConfig + "/aurionsvotelistener.conf").toFile();
		FileInputStream fis2 = new FileInputStream(old2);
		BufferedReader in2 = new BufferedReader(new InputStreamReader(fis2));
		
		File outF2 = new File(defaultConfig + "/AdvancedReward.conf");
		FileWriter fstream2 = new FileWriter(outF2, true);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		
		
		count = 0;
		while ((aLine = in2.readLine()) != null) {
			if (aLine.startsWith("ExtraReward") || aLine.startsWith("cumulativevoting"))
				copy = true;
			if (copy && aLine.contains("{")) {
				count += 1;
			}
			if (copy && aLine.contains("}")) {
				count -= 1;
			}
			if (copy && count == 0) {
				copyafter = true;
				copy = false;
			}
			if (copy) {
				out2.write(aLine);
				out2.newLine();
			}
			if (copyafter) {
				out2.write(aLine);
				out2.newLine();
				copyafter = false;
			}
		}
		
		out2.write("perms{");
		out2.newLine();
		out2.write("\"Aurions.example\"=[");
		out2.newLine();
		out2.write("\"give <username> stone 5\"");
		out2.newLine();
		out2.write("]");
		out2.newLine();
		out2.write("}");
		out2.newLine();
		
		out2.close();
		in2.close();
		
		AurionsVoteListener.GetInstance().settingNode = AurionsVoteListener.settingLoader.load();
		AurionsVoteListener.GetInstance().rewardNode = AurionsVoteListener.rewardLoader.load();
		AurionsVoteListener.GetInstance().adrewardNode = AurionsVoteListener.adrewardLoader.load();
		
		AurionsVoteListener.GetInstance().getSetting().getNode("Version").setValue(9);
		AurionsVoteListener.GetInstance().saveConfig();

	}
	/*
	 * public static void version8(ConfigurationLoader<CommentedConfigurationNode>
	 * loader) throws IOException { CommentedConfigurationNode rootNode =
	 * loader.load(); rootNode.getNode("Version").setValue(8);
	 * loader.save(rootNode); }
	 * 
	 * public static void version7(ConfigurationLoader<CommentedConfigurationNode>
	 * loader) throws IOException { Asset v7 =
	 * plugin.getAsset("Version7.conf").get(); File fin = new
	 * File(defaultConfig.toString()); FileWriter fstream = new FileWriter(fin,
	 * true); BufferedWriter out = new BufferedWriter(fstream);
	 * 
	 * out.write(v7.readString()); out.close();
	 * 
	 * CommentedConfigurationNode rootNode = loader.load();
	 * 
	 * rootNode.getNode("Version").setValue(7);
	 * 
	 * loader.save(rootNode); }
	 * 
	 * public static void version6(ConfigurationLoader<CommentedConfigurationNode>
	 * loader) throws IOException {
	 * 
	 * Asset v6 = plugin.getAsset("Version6.conf").get(); File fin = new
	 * File(defaultConfig.toString()); FileWriter fstream = new FileWriter(fin,
	 * true); BufferedWriter out = new BufferedWriter(fstream);
	 * 
	 * out.write(v6.readString()); out.close();
	 * 
	 * CommentedConfigurationNode rootNode = loader.load();
	 * AurionsVoteListener.GetInstance().old = true;
	 * 
	 * rootNode.getNode("Version").setValue(6);
	 * rootNode.getNode("settings","cumulativevoting").setValue(false);
	 * 
	 * loader.save(rootNode);
	 * 
	 * 
	 * }
	 */
}
