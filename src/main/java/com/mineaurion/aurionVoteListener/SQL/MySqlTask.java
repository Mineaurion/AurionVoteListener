package com.mineaurion.aurionVoteListener.SQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionVoteListener.Config;



public class MySqlTask {
	public static SqlService sql;
	public static String TableTotal;
	public static String TableQueue;

	public synchronized void open(String dbHost, int dbPort, String dbUser, String dbPass, String dbName, String dbPrefix, Game game) {
		try {		
			TableTotal = Config.dbTableTotal;
			TableQueue = Config.dbTableQueue;
			
			sql = game.getServiceManager().provide(SqlService.class).get();
			
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection to database"));
			Sponge.getServer().getConsole().sendMessage(Text.of(dbHost));
	
			SwitchSQL.datasource = sql.getDataSource("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPass);
			
			Database requete = new Database();
			
			Connection connection = SwitchSQL.datasource.getConnection();
			
			Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection succesfull"));
			
			
			
			
	
			if (!requete.tableExists(dbPrefix + TableTotal, connection)) {
				requete.modifyQuery("CREATE TABLE `" + dbPrefix + TableTotal + "` (`IGN` varchar(32) NOT NULL, `votes` int(10) DEFAULT 0, `lastvoted` BIGINT(16) DEFAULT 0, PRIMARY KEY (`IGN`));", connection);
			} else {
				String query = "SELECT `lastvoted` FROM `" + dbPrefix + TableTotal + "` LIMIT 1;";
				requete.readQuery(query, connection);
				try {
					Statement stmt = connection.createStatement();
					stmt.executeQuery(query);
					stmt.close();
				} catch (SQLException e) {
					requete.modifyQuery("ALTER TABLE `" + dbPrefix + TableTotal	+ "` ADD  `lastvoted` BIGINT(16) DEFAULT 0 AFTER `votes`;", connection);
				}
			}
			
			if (!requete.tableExists(dbPrefix + TableQueue, connection)) {
				requete.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue + "` (`IGN` varchar(32) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(200));", connection);
			}
			
		} catch (SQLException e) { 
			e.printStackTrace(); 
		} 
	}
}