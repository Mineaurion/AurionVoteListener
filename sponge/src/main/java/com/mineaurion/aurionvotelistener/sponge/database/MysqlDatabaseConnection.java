package com.mineaurion.aurionvotelistener.sponge.database;

import com.mineaurion.aurionvotelistener.sponge.config.Config;

import java.sql.SQLException;

public class MysqlDatabaseConnection extends DatabaseConnection {

    private static final String UrlFormat = "jdbc:mysql://%s:%s@%s:%d/%s";

    /**
     * Open Mysql database connection
     *
     * @param mysql Mysql config object
     * @throws SQLException
     */
    public MysqlDatabaseConnection(Config settings) throws SQLException{
        super(
                String.format(
                    UrlFormat,
                    settings.database.user,
                    settings.database.pass,
                    settings.database.host,
                    settings.database.port,
                    settings.database.name
                )
        );
    }

}
