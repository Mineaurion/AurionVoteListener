package com.mineaurion.aurionVoteListener.SQL;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	public boolean tableExists(String table, Connection connection) throws SQLException {
		
		DatabaseMetaData dmd = connection.getMetaData();
		ResultSet rs = dmd.getTables(null, null, table, null);
		if (rs.next()) {
		  rs.close();
		  return true;
		}
		rs.close();
		return false;	  
	}
	
	public synchronized void modifyQuery(String query, Connection connection) throws SQLException {
	  
		Statement stmt = connection.createStatement();
	    stmt.execute(query); 
	    stmt.close();
	   
	  }
	
	public ResultSet readQuery(String query, Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeQuery(query);

	}
}
