package com.mineaurion.aurionvotelistener.sponge.database;

import com.mineaurion.aurionvotelistener.sponge.config.Config;

import java.sql.SQLException;

public class SqliteDatabaseConnection extends DatabaseConnection {

    private static final String UrlFormat = "jdbc:sqlite:%s";


    /**
     * Open a sqlite connection
     *
     * @param sqlite storage of the database file
     * @throws SQLException
     */
    public SqliteDatabaseConnection(String file) throws SQLException{
        super(String.format(UrlFormat, file));
    }
}
