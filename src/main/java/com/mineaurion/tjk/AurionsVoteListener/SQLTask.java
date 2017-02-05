package com.mineaurion.tjk.AurionsVoteListener;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

public class SQLTask {
	public static Connection connection;
	public static AurionsVoteListener plugin = new AurionsVoteListener();
	public static String TableTotal;
	public static String TableQueue;
	
	public synchronized static Connection open(String file, String Dir,String dbPrefix){
		File SQLFile = new File(Dir+File.separator+file);
		Database sql = new Database();
		
		
		TableTotal = AurionsVoteListener.GetInstance().dbTableTotal;
		TableQueue = AurionsVoteListener.GetInstance().dbTableQueue;
		Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection to database"));
		
		
		try
		{	
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite://"+SQLFile);
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection succesfull"));
		}
		catch (Exception e)
		{
			Sponge.getServer().getConsole().sendMessage(Text.of(e.toString()));
		}
		
		 if (!sql.tableExists(dbPrefix + TableTotal, connection))
	      {
	        sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableTotal+"` (`IGN` VARCHAR UNIQUE, `votes` INTEGER DEFAULT 0, `lastvoted` INTEGER DEFAULT 0);", connection);
	      }
		 else
	      {
	        String query = "SELECT `lastvoted` FROM `" + dbPrefix + TableTotal+"` LIMIT 1;";
	        sql.readQuery(query, connection);
	        try
	        {
	          Statement stmt = connection.createStatement();
	          stmt.executeQuery(query);
	          stmt.close();
	        }
	        catch (SQLException e)
	        {
	          sql.modifyQuery("ALTER TABLE `" + dbPrefix + TableTotal+"` ADD  `lastvoted` INTEGER DEFAULT 0;", connection);
	        }
	      }
	      if (!sql.tableExists(dbPrefix + TableQueue, connection)) {
	        sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue+"` (`IGN` VARCHAR, `service` VARCHAR, `timestamp` VARCHAR, `ip` VARCHAR);", connection);
	      }
		return connection;
	}
}
