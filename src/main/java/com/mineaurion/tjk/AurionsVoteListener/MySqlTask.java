package com.mineaurion.tjk.AurionsVoteListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

public class MySqlTask {
	public static Connection connection;
	public static AurionsVoteListener plugin = new AurionsVoteListener();
	public static String TableTotal;
	public static String TableQueue;
	
	public synchronized static Connection open(String dbHost, int dbPort, String dbUser, String dbPass, String dbName, String dbPrefix){
		Database sql = new Database();
		TableTotal = AurionsVoteListener.GetInstance().dbTableTotal;
		TableQueue = AurionsVoteListener.GetInstance().dbTableQueue;
		Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection to database"));
		Sponge.getServer().getConsole().sendMessage(Text.of(dbHost));
		
		try
		{	
			connection = DriverManager.getConnection("jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName,dbUser,dbPass);
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection succesfull"));
		}
		catch (Exception e)
		{
			Sponge.getServer().getConsole().sendMessage(Text.of(e.toString()));
		}
		
		 if (!sql.tableExists(dbPrefix + TableTotal, connection))
	      {
	        sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableTotal+"` (`IGN` varchar(16) NOT NULL, `votes` int(10) DEFAULT 0, `lastvoted` BIGINT(16) DEFAULT 0, PRIMARY KEY (`IGN`));", connection);
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
	          sql.modifyQuery("ALTER TABLE `" + dbPrefix + TableTotal+"` ADD  `lastvoted` BIGINT(16) DEFAULT 0 AFTER `votes`;", connection);
	        }
	      }
	      if (!sql.tableExists(dbPrefix + TableQueue, connection)) {
	        sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue+"` (`IGN` varchar(16) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(32));", connection);
	      }
		return connection;
	}
}

	 

