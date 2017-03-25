package com.mineaurion.tjk.AurionsVoteListener;


import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.serializer.TextSerializers;


public class SwitchSQL {
	public static Connection connection;
	public static String SQLTYPE;
	public static String SQLFILE;
	public static Path ConfigDir;
	public static AurionsVoteListener plugin = new AurionsVoteListener();
	public static String TableTotal;
	public static String TableQueue;
	
	public synchronized static void open(String dbHost, int dbPort, String dbUser, String dbPass, String dbName, String dbPrefix){
		 SQLTYPE = AurionsVoteListener.GetInstance().SQLType;
		TableTotal = AurionsVoteListener.GetInstance().dbTableTotal;
		TableQueue = AurionsVoteListener.GetInstance().dbTableQueue;
		SQLFILE = AurionsVoteListener.GetInstance().SQLFile;
		ConfigDir = AurionsVoteListener.GetInstance().ConfigDir;
		if(SQLTYPE.equals("MySQL")){
			connection = MySqlTask.open(dbHost, dbPort, dbUser, dbPass, dbName, dbPrefix);
		}else if(SQLTYPE.equals("File")){
			connection = SQLTask.open(SQLFILE,ConfigDir.toString(),dbPrefix);
		}
		else{
			Sponge.getGame().getServer().getConsole().sendMessage(TextSerializers.formattingCode('§').deserialize("[AurionsVoteListener] §cPlease choose between mysql and file in the config file "));
		}
		
	}

	public static void Close()
	{
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized int TotalsVote(String name){
		
			PreparedStatement sql;
			int votePlayer = 0;
			try {
				
				sql = connection.prepareStatement("SELECT `votes` FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"` WHERE `IGN`=?");
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
	
	public static synchronized void VoteTop(CommandSource src){
			
				PreparedStatement sql;
				int place = 1;
				try {
					
					sql = connection.prepareStatement("SELECT * FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"` ORDER BY `votes` DESC LIMIT ?");
					sql.setLong(1, plugin.votetopnumber);
					ResultSet resultSet = sql.executeQuery();
					while(resultSet.next()){
						String user = resultSet.getString(1);
						int total = resultSet.getInt(2);
						String message = plugin.votetopformat.replace("<POSITION>", String.valueOf(place)).replace("<TOTAL>", String.valueOf(total)).replace("<username>", user);
						src.sendMessage(plugin.formatmessage(message,"", src.getName()));
						place++;
					}
					sql.close();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
				
		}
		
	public static synchronized boolean Cleartotals(){
			
			PreparedStatement sql;
			
			try {
				
				sql = connection.prepareStatement("DELETE FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"`");
				sql.execute();
				sql.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		
	public static synchronized boolean Clearqueue(){
			
			PreparedStatement sql;
			
			try {
				
				sql = connection.prepareStatement("DELETE FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"`");
				sql.execute();
				sql.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
		}

	public static synchronized boolean Voted(String player, int totalvotes,long now){
	
		PreparedStatement sql = null;
		
		try {
			if(SQLTYPE.equals("MySQL")){
			sql = connection.prepareStatement("INSERT INTO `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"` (`IGN`, `votes`, `lastvoted`) VALUES ('" + player + "', " + totalvotes + ", " + now + ") ON DUPLICATE KEY UPDATE `votes` = " + totalvotes + ", `lastvoted` = " + now + ", `IGN` = '" + player + "';");
			sql.executeQuery();
			}else if(SQLTYPE.equals("File")){
				sql = connection.prepareStatement("INSERT OR IGNORE INTO `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"` (`IGN`, `votes`, `lastvoted`) VALUES ('"+ player + "', " + totalvotes + ", " + now + ");");
				sql.execute();
				sql = connection.prepareStatement("UPDATE `" + AurionsVoteListener.GetInstance().dbPrefix + TableTotal+"` SET `votes` = " + totalvotes + ", `lastvoted` = " + now + " WHERE `IGN` = '" + player + "';");
				sql.execute();
			}
			sql.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
			
		}
	}

	public static void offline(String username, String serviceName, String timeStamp, String address) {
		PreparedStatement sql = null;
		try {
			if(SQLTYPE.equals("MySQL")){
				sql = connection.prepareStatement("INSERT INTO `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('"+username+"', '"+serviceName+"', '"+ timeStamp+"', '"+address+"');");
			sql.executeQuery();
			}else if(SQLTYPE.equals("File")){
				sql = connection.prepareStatement("INSERT OR IGNORE INTO `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` (`IGN`, `service`, `timestamp`, `ip`) VALUES ('"+username+"', '"+serviceName+"', '"+ timeStamp+"', '"+address+"');");
				sql.execute();
				sql = connection.prepareStatement("UPDATE `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` SET `IGN` = " + username + ", `service` = " + serviceName+ ", `timestamp` = '" + timeStamp + ", `ip` = '" + address + "';");
				sql.execute();
			}
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean QueueUsername(String username){
		PreparedStatement sql;
		try {
			sql = connection.prepareStatement("SELECT * FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` WHERE `IGN`=?");
			sql.setString(1, username);
			ResultSet resultSet = sql.executeQuery();
			if(!resultSet.next()){
				return false;
			}
			else{
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public static List<String> QueueReward(String username){
		PreparedStatement sql;
		List<String> service = new ArrayList<String>();
		try {
			
			sql = connection.prepareStatement("SELECT `service` FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` WHERE `IGN`=?");
			sql.setString(1, username);
			ResultSet resultSet = sql.executeQuery();
			while (resultSet.next()) {
				service.add(resultSet.getString("service"));
			}
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return service;
		
	}

	public static void removeQueue(String username, String service) {
		PreparedStatement sql;
		try {
			
			sql = connection.prepareStatement("DELETE FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"` WHERE `IGN`=? AND `service`=?");
			sql.setString(1, username);
			sql.setString(2, service);
			sql.execute();
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<String> QueuePlayer(){
		PreparedStatement sql;
		List<String> player = new ArrayList<String>();
		try {
			
			sql = connection.prepareStatement("SELECT `IGN` FROM `" + AurionsVoteListener.GetInstance().dbPrefix + TableQueue+"`");
			ResultSet resultSet = sql.executeQuery();
			while (resultSet.next()) {
				player.add(resultSet.getString("IGN"));
			}
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return player;
	}
	
	
}

	 

