package com.mineaurion.aurionvotelistener.SQL;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionvotelistener.Config;
import com.mineaurion.aurionvotelistener.SQL.Database;

public class SQLTask {
	public static SqlService sql;
	public static String TableTotal;
	public static String TableQueue;
	
	public synchronized void open(String file, String Dir,String dbPrefix, Game game) {
		try {
			TableTotal = Config.dbTableTotal;
			TableQueue = Config.dbTableQueue;
			
			sql = game.getServiceManager().provide(SqlService.class).get();
			
			File SQLFile = new File(Dir+File.separator+file);
			
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection to database"));
			SwitchSQL.datasource = sql.getDataSource("jdbc:sqlite://"+SQLFile);
		
			Database requete = new Database();
			
			Connection connection = SwitchSQL.datasource.getConnection();
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection succesfull"));
		
			if (!requete.tableExists(dbPrefix + TableTotal, connection)){
				requete.modifyQuery("CREATE TABLE `" + dbPrefix + TableTotal+"` (`IGN` VARCHAR UNIQUE, `votes` INTEGER DEFAULT 0, `lastvoted` INTEGER DEFAULT 0);", connection);
		     } else {
		        String query = "SELECT `lastvoted` FROM `" + dbPrefix + TableTotal+"` LIMIT 1;";
		        requete.readQuery(query, connection);
		        try
		        {
		          Statement stmt = connection.createStatement();
		          stmt.executeQuery(query);
		          stmt.close();
		        } catch (SQLException e) {
		        	requete.modifyQuery("ALTER TABLE `" + dbPrefix + TableTotal+"` ADD  `lastvoted` INTEGER DEFAULT 0;", connection);
		        }
		      }
			 
			 if (!requete.tableExists(dbPrefix + TableQueue, connection)) {
				 requete.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue+"` (`IGN` VARCHAR, `service` VARCHAR, `timestamp` VARCHAR, `ip` VARCHAR);", connection);
			 }
			 connection.close();
		} catch (SQLException e) { 
			e.printStackTrace(); 
		} 
	}
}
