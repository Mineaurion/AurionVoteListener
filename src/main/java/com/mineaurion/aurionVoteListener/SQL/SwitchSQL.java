package com.mineaurion.aurionVoteListener.SQL;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.mineaurion.aurionVoteListener.Config;
import com.mineaurion.aurionVoteListener.Main;




public class SwitchSQL {
	private Main plugin;
	
	public static Connection connection;
	

	public SwitchSQL(Main main) {
		plugin = main;
		
	}

	public synchronized void open() throws SQLException 
	{
		if (Config.SQLType.equalsIgnoreCase("MySQL")) {
			connection = plugin.mysqltask.open(Config.dbHost, Config.dbPort, Config.dbUser, Config.dbPass, Config.dbName, Config.dbPrefix);
		} else if (Config.SQLType.equalsIgnoreCase("File")) {
			connection = plugin.sqltask.open(Config.SQLFile, plugin.configDir.toString(), Config.dbPrefix);
		} else {
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.formattingCode('§').deserialize("[AurionsVoteListener] §cPlease choose between mysql and file in the config file "));
		}
	}

	public void Close() throws SQLException
	{	
		connection.close();
	}
	
	public synchronized int TotalsVote(String name)
	{
		PreparedStatement sql;
		int votePlayer = 0;
		
		try {
			sql = connection.prepareStatement("SELECT `votes` FROM `" + Config.dbPrefix + Config.dbTableTotal + "` WHERE `IGN`=?");
			sql.setString(1, name);
			ResultSet resultSet = sql.executeQuery();
			while (resultSet.next()) {
				votePlayer = resultSet.getInt("votes");
			}
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return votePlayer;
	}

	public synchronized void VoteTop(CommandSource src) throws SQLException {

		PreparedStatement sql;
		int place = 1;

		sql = connection.prepareStatement("SELECT * FROM `" + Config.dbPrefix + Config.dbTableTotal + "` ORDER BY `votes` DESC LIMIT ?");
		sql.setLong(1, Config.votetopnumber);
		ResultSet resultSet = sql.executeQuery();
		while (resultSet.next()) {
			String user = resultSet.getString(1);
			int total = resultSet.getInt(2);
			String message = Config.votetopformat.replace("<POSITION>", String.valueOf(place)).replace("<TOTAL>", String.valueOf(total)).replace("<username>", user);
			src.sendMessage(plugin.formatmessage(message, "", src.getName()));
			place++;
		}
		sql.close();
	}

	public  synchronized boolean Cleartotals() {

		PreparedStatement sql;

		try {
			sql = connection.prepareStatement("DELETE FROM `" + Config.dbPrefix + Config.dbTableTotal + "`");
			sql.execute();
			sql.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean Clearqueue() {

		PreparedStatement sql;

		try {
			sql = connection.prepareStatement("DELETE FROM `" + Config.dbPrefix + Config.dbTableQueue + "`");
			sql.execute();
			sql.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized boolean Voted(String player, int totalvotes, long now) {

		PreparedStatement sql = null;

		try {
			if (Config.SQLType.equals("MySQL")) {
				sql = connection.prepareStatement(
						"INSERT INTO `" + Config.dbPrefix + Config.dbTableTotal + "` (`IGN`, `votes`, `lastvoted`) VALUES ('"
						+ player + "', " + totalvotes + ", " + now + ") ON DUPLICATE KEY UPDATE `votes` = "
						+ totalvotes + ", `lastvoted` = " + now + ", `IGN` = '" + player + "';");
				sql.executeQuery();			
			} else if (Config.SQLType.equals("File")) {
				sql = connection.prepareStatement("INSERT OR IGNORE INTO `" + Config.dbPrefix + Config.dbTableTotal
						+ "` (`IGN`, `votes`, `lastvoted`) VALUES ('" + player + "', " + totalvotes + ", " + now
						+ ");");
				sql.execute();
				
				sql = connection.prepareStatement("UPDATE `" + Config.dbPrefix + Config.dbTableTotal + "` SET `votes` = "
						+ totalvotes + ", `lastvoted` = " + now + " WHERE `IGN` = '" + player + "';");
				sql.execute();
			}
			sql.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void offline(String username, String serviceName, String timeStamp, String address) throws SQLException {
		PreparedStatement sql = null;
		
			if (Config.SQLType.equals("MySQL")) {
				sql = connection.prepareStatement("INSERT INTO `" + Config.dbPrefix + Config.dbTableQueue
						+ "` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('" + username + "', '" + serviceName + "', '"
						+ timeStamp + "', '" + address + "');");
				sql.executeQuery();
			} else if (Config.SQLType.equals("File")) {
				sql = connection.prepareStatement("INSERT OR IGNORE INTO `" + Config.dbPrefix + Config.dbTableQueue
						+ "` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('" + username + "', '" + serviceName + "', '"
						+ timeStamp + "', '" + address + "');");
				sql.execute();
			}
			sql.close();


	}

	public boolean QueueUsername(String username) {
		PreparedStatement sql;
		try {
			sql = connection.prepareStatement("SELECT * FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`=?");
			sql.setString(1, username);
			ResultSet resultSet = sql.executeQuery();
			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String> QueueReward(String username) throws SQLException {
		PreparedStatement sql;
		List<String> service = new ArrayList<String>();

		sql = connection.prepareStatement("SELECT `service` FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`=?");
		sql.setString(1, username);
		ResultSet resultSet;
		resultSet = sql.executeQuery();
		while (resultSet.next()) {
			service.add(resultSet.getString("service"));
		}
		sql.close();

		return service;

	}

	public void removeQueue(String username, String service) throws SQLException {
		PreparedStatement sql;
		
		sql = connection.prepareStatement("DELETE FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`=? AND `service`=?");
		sql.setString(1, username);
		sql.setString(2, service);
		sql.execute();
		sql.close();
	}

	public List<String> QueuePlayer() throws SQLException {
		PreparedStatement sql;
		List<String> player = new ArrayList<String>();
		
		sql = connection.prepareStatement("SELECT `IGN` FROM `" + Config.dbPrefix + Config.dbTableQueue + "`");
		ResultSet resultSet = sql.executeQuery();
		while (resultSet.next()) {
			player.add(resultSet.getString("IGN"));
		}
		sql.close();
		
		return player;
	}

}
