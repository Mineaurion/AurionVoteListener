package com.mineaurion.aurionVoteListener.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionVoteListener.Config;



public class MySqlTask {
	public static Connection connection;
	public static String TableTotal;
	public static String TableQueue;

	public synchronized Connection open(String dbHost, int dbPort, String dbUser, String dbPass, String dbName, String dbPrefix) throws SQLException {
		Database sql = new Database();
		TableTotal = Config.dbTableTotal;
		TableQueue = Config.dbTableQueue;
		Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection to database"));
		Sponge.getServer().getConsole().sendMessage(Text.of(dbHost));

		connection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName, dbUser, dbPass);
		Sponge.getServer().getConsole().sendMessage(Text.of(">> Connection succesfull"));
		

		if (!sql.tableExists(dbPrefix + TableTotal, connection)) {
			sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableTotal + "` (`IGN` varchar(32) NOT NULL, `votes` int(10) DEFAULT 0, `lastvoted` BIGINT(16) DEFAULT 0, PRIMARY KEY (`IGN`));", connection);
		} else {
			String query = "SELECT `lastvoted` FROM `" + dbPrefix + TableTotal + "` LIMIT 1;";
			sql.readQuery(query, connection);
			try {
				Statement stmt = connection.createStatement();
				stmt.executeQuery(query);
				stmt.close();
			} catch (SQLException e) {
				sql.modifyQuery("ALTER TABLE `" + dbPrefix + TableTotal	+ "` ADD  `lastvoted` BIGINT(16) DEFAULT 0 AFTER `votes`;", connection);
			}
		}
		
		if (!sql.tableExists(dbPrefix + TableQueue, connection)) {
			sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue + "` (`IGN` varchar(32) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(200));", connection);
		}
		return connection;
		
		/*
		if (Main.GetInstance().old) {
			sql.modifyQuery("RENAME TABLE `" + dbPrefix + TableQueue + "` TO `" + dbPrefix + TableQueue + "old`;",connection);
			sql.modifyQuery("CREATE TABLE `" + dbPrefix + TableQueue + "` (`IGN` varchar(32) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(200));", connection);

			sql.modifyQuery("INSERT INTO `" + dbPrefix + TableQueue	+ "`(IGN,service,timestamp,ip) SELECT IGN,service,timestamp,ip FROM `" + dbPrefix + TableQueue + "`;", connection);
			sql.modifyQuery("DROP TABLE `" + dbPrefix + TableQueue + "old`;", connection);
		}
*/
		
		
	}
}
