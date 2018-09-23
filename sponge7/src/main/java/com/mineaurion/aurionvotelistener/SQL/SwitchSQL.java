package com.mineaurion.aurionvotelistener.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.mineaurion.aurionvotelistener.Config;
import com.mineaurion.aurionvotelistener.Main;

public class SwitchSQL {
	private Main plugin;
	private Game game;

	public static DataSource datasource;

	public SwitchSQL(Main main, Game games) {
		plugin = main;
		game = games;
	}

	public synchronized void open() {
		if (Config.SQLType.equalsIgnoreCase("MySQL")) {
			plugin.mysqltask.open(Config.dbHost, Config.dbPort, Config.dbUser, Config.dbPass, Config.dbName,
					Config.dbPrefix, game);
		} else if (Config.SQLType.equalsIgnoreCase("File")) {
			plugin.sqltask.open(Config.SQLFile, plugin.configDir.toString(), Config.dbPrefix, game);
		} else {
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize("[AurionsVoteListener] &cPlease choose between mysql and file in the config file "));
		}
	}

	public synchronized int TotalsVote(String name) {
		int votePlayer = 0;
		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection.prepareStatement(
						"SELECT `votes` FROM `" + Config.dbPrefix + Config.dbTableTotal + "` WHERE `IGN`=?");) {

			sql.setString(1, name);
			try (ResultSet resultSet = sql.executeQuery();) {
				while (resultSet.next()) {
					votePlayer = resultSet.getInt("votes");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return votePlayer;
	}

	public synchronized void VoteTop(CommandSource src) {

		int place = 1;
		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection.prepareStatement("SELECT * FROM `" + Config.dbPrefix
						+ Config.dbTableTotal + "` ORDER BY `votes` DESC LIMIT ?");) {

			sql.setLong(1, Config.votetopnumber);
			try (ResultSet resultSet = sql.executeQuery();) {
				while (resultSet.next()) {
					String user = resultSet.getString(1);
					int total = resultSet.getInt(2);
					String message = Config.votetopformat.replace("<POSITION>", String.valueOf(place))
							.replace("<TOTAL>", String.valueOf(total)).replace("<username>", user);
					src.sendMessage(plugin.formatmessage(message, "", src.getName()));
					place++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean Cleartotals() {
		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection
						.prepareStatement("DELETE FROM `" + Config.dbPrefix + Config.dbTableTotal + "`");) {
			sql.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean Clearqueue() {
		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection
						.prepareStatement("DELETE FROM `" + Config.dbPrefix + Config.dbTableQueue + "`");) {

			sql.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized boolean Voted(String player, int totalvotes, long now) {

		try (Connection connection = datasource.getConnection(); 
				Statement sql = connection.createStatement();){
			if (Config.SQLType.equals("MySQL")) {
				sql.executeUpdate("INSERT INTO `" + Config.dbPrefix + Config.dbTableTotal
						+ "` (`IGN`, `votes`, `lastvoted`) VALUES ('" + player + "', " + totalvotes + ", " + now
						+ ") ON DUPLICATE KEY UPDATE `votes` = " + totalvotes + ", `lastvoted` = " + now + ", `IGN` = '"
						+ player + "';");
			} else if (Config.SQLType.equals("File")) {
				sql.executeUpdate("INSERT OR IGNORE INTO `" + Config.dbPrefix + Config.dbTableTotal
						+ "` (`IGN`, `votes`, `lastvoted`) VALUES ('" + player + "', " + totalvotes + ", " + now
						+ ");");
				sql.executeUpdate("UPDATE `" + Config.dbPrefix + Config.dbTableTotal + "` SET `votes` = " + totalvotes
						+ ", `lastvoted` = " + now + " WHERE `IGN` = '" + player + "';");
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void offline(String username, String serviceName, String timeStamp, String address) {
		try (Connection connection = datasource.getConnection(); 
			Statement sql = connection.createStatement();){	
			if (Config.SQLType.equals("MySQL")) {
				sql.executeUpdate("INSERT INTO `" + Config.dbPrefix + Config.dbTableQueue
						+ "` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('" + username + "', '" + serviceName + "', '"
						+ timeStamp + "', '" + address + "');");
			} else if (Config.SQLType.equals("File")) {
				sql.executeUpdate("INSERT OR IGNORE INTO `" + Config.dbPrefix + Config.dbTableQueue
						+ "` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('" + username + "', '" + serviceName + "', '"
						+ timeStamp + "', '" + address + "');");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean QueueUsername(String username) {
		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection.prepareStatement(
						"SELECT * FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`=?");) {
			sql.setString(1, username);
			try (ResultSet resultSet = sql.executeQuery();) {
				if (!resultSet.next()) {
					return false;
				} else {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String> QueueReward(String username) {

		List<String> service = new ArrayList<String>();

		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection.prepareStatement(
						"SELECT `service` FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`=?");) {
			sql.setString(1, username);
			try (ResultSet resultSet = sql.executeQuery();) {
				while (resultSet.next()) {
					service.add(resultSet.getString("service"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return service;
	}

	public void removeQueue(String username, String service) {
		try (Connection connection = datasource.getConnection();
				Statement sql = connection.createStatement();) 
		{
			String querry  = "DELETE FROM `" + Config.dbPrefix + Config.dbTableQueue + "` WHERE `IGN`='"+username+"' AND `service`='"+service+"';";
			sql.executeUpdate(querry);
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> QueuePlayer() {

		List<String> player = new ArrayList<String>();

		try (Connection connection = datasource.getConnection();
				PreparedStatement sql = connection
						.prepareStatement("SELECT `IGN` FROM `" + Config.dbPrefix + Config.dbTableQueue + "`");
				ResultSet resultSet = sql.executeQuery();) {

			while (resultSet.next()) {
				player.add(resultSet.getString("IGN"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return player;
	}
}
