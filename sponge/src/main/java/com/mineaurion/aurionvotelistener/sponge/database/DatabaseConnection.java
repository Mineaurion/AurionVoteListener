package com.mineaurion.aurionvotelistener.sponge.database;

import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;

public abstract class DatabaseConnection {

    private AurionVoteListener plugin;

    public DatabaseConnection(AurionVoteListener plugin){
        this.plugin = plugin;
    }

    public static final int DEFAULT_MYSQL_PORT = 3306;

    private String connectionUrl;


    private SqlService getSql(){
        return Sponge.getServiceManager().provide(SqlService.class).get();
    }

    protected DataSource getDataSource(String jdbcUrl) throws SQLException {
        return getSql().getDataSource(jdbcUrl);
    }

    protected DatabaseConnection(String connectionUrl) throws SQLException {
        this.connectionUrl = connectionUrl;
        connect();
    }

    private void connect() throws SQLException{
        getDataSource();
    }

    private DataSource getDataSource() throws SQLException{
        return getDataSource(connectionUrl);
    }

    public Connection getConnection() throws SQLException{
        return getDataSource().getConnection();
    }

    public Statement getStatement() throws SQLException{
        return getConnection().createStatement();
    }

    public PreparedStatement getPreparedStatement(String statement) throws SQLException{
        return getConnection().prepareStatement(statement);
    }

    public ResultSet executeQuery(String query) throws SQLException{
        try(Statement statement = getStatement();
            Connection connection = statement.getConnection()) {
            return statement.executeQuery(query);
        }
    }

    public boolean executeStatement(String query) throws SQLException{
        try(Statement statement = getStatement();
        Connection connection = statement.getConnection()) {
            return statement.execute(query);
        }
    }

    public int executeUpdate(String query) throws SQLException {
        try(Statement statement = getStatement();
        Connection connection = statement.getConnection()) {
            return statement.executeUpdate(query);
        }
    }

    public boolean tableExist(String table) throws SQLException{
        Statement statement = getStatement();
        Connection connection = statement.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet rs = databaseMetaData.getTables(null, null, table, null);
        if(rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }




}
