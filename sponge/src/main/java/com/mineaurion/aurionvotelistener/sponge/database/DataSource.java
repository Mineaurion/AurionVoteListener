package com.mineaurion.aurionvotelistener.sponge.database;

import com.mineaurion.aurionvotelistener.sponge.config.Config;
import com.mineaurion.aurionvotelistener.sponge.AurionVoteListener;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {

    private AurionVoteListener plugin;
    private Config config;

    private DatabaseConnection connection;

    private String storageConfig;
    private String tableTotal;
    private String tableQueue;


    public DataSource(AurionVoteListener plugin) throws SQLException{
        this.plugin = plugin;
        this.config = plugin.getConfig();
        storageConfig = config.database.storage;
        tableTotal = config.database.prefix + config.database.tableTotal;
        tableQueue = config.database.prefix + config.database.tableQueue;

        if(storageConfig.equalsIgnoreCase("mysql")){
            connection = new MysqlDatabaseConnection(config);
        }
        else if(storageConfig.equalsIgnoreCase("sqlite")){
            connection = new SqliteDatabaseConnection(plugin.configDir + File.separator + config.database.file);
        }
        else{
            throw new IllegalArgumentException("Invalid Storage engine!");
        }
        prepareTable();
    }


    private synchronized void prepareTable(){
        try{
            String createTableTotal = "CREATE TABLE `" + tableTotal + "` (`IGN` varchar(32) NOT NULL, `votes` int(10) DEFAULT 0, `lastvoted` BIGINT(16) DEFAULT 0, PRIMARY KEY (`IGN`));";
            String createTableQueue = "CREATE TABLE `" + tableQueue + "` (`IGN` varchar(32) NOT NULL,`service` varchar(64), `timestamp` varchar(32), `ip` varchar(200));";
            if(!connection.tableExist(tableTotal)){
                connection.executeStatement(createTableTotal);
                connection.getStatement().close();
                connection.getConnection().close();
                plugin.getLogger().info("Table created");
            }
            if(!connection.tableExist(tableQueue)){
                connection.executeStatement(createTableQueue);
                connection.getStatement().close();
                connection.getConnection().close();
                plugin.getLogger().info("Table created");
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("Could not create Table!", e);
        }
    }

    public int totalsVote(String name){
        int votePlayer = 0;
        try(
            PreparedStatement sql = connection.getConnection().prepareStatement(
                String.format("SELECT votes FROM %s WHERE `IGN`=?", tableTotal)
            );
            Connection connection = sql.getConnection()
        ){
            sql.setString(1, name);
            try(ResultSet resultSet = sql.executeQuery()){
                while (resultSet.next()) {
                    votePlayer = resultSet.getInt("votes");
                }
            }
        }
        catch(SQLException e){
            plugin.getLogger().error("SQL error", e);
        }
        return votePlayer;
    }

    public String voteTop(){
        int place = 1;
        StringBuilder message = new StringBuilder();
        String messageFormat = config.settings.voteTop.format;
        try(
            PreparedStatement sql = connection.getConnection().prepareStatement(
                    String.format("SELECT * FROM %s ORDER BY `votes` DESC limit ?", tableTotal)
            );
            Connection connection = sql.getConnection();
        ){
            sql.setLong(1, config.settings.voteTop.number);
            try(ResultSet resultSet = sql.executeQuery()) {
                while (resultSet.next()){
                    String user = resultSet.getString(1);
                    String total = String.valueOf(resultSet.getInt(2));
                    message
                        .append(
                            messageFormat
                                .replace("<POSITION>", String.valueOf(place))
                                .replace("<TOTAL>", String.valueOf(total))
                                .replace("<username>", user)
                        )
                        .append("\n");
                    place++;
                }
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL error", e);
        }
        return message.toString();
    }

    public synchronized void clearTotals(){
        try(
            PreparedStatement sql = connection.getConnection().prepareStatement(String.format("DELETE FROM `%s`", tableTotal));
            Connection connection = sql.getConnection();
        ){
            sql.execute();
        }
        catch (SQLException e){
            plugin.getLogger().error("Clear Total Failed", e);
        }
    }

    public synchronized void clearQueue(){
        try(
            PreparedStatement sql = connection.getConnection().prepareStatement(String.format("DELETE FROM `%s`", tableQueue));
            Connection connection = sql.getConnection();
        ){
            sql.execute();
        }
        catch (SQLException e){
            plugin.getLogger().error("Clear Queue Failed", e);
        }
    }

    public synchronized void voted(String player, int totalVotes, long now){
        if(config.database.storage.equalsIgnoreCase("sqlite")){
            try(
                PreparedStatement sql = connection.getPreparedStatement(
                    String.format("INSERT OR REPLACE INTO %s (IGN, votes, lastvoted) " + "VALUES (?, (SELECT Case When exists(SELECT 1 FROM %s WHERE IGN=?)THEN ? ELSE ? END), ?)", tableTotal, tableTotal)
                );
                Connection connection = sql.getConnection();
            ) {
                sql.setString(1, player);
                sql.setString(2, player);
                sql.setInt(3, totalVotes);
                sql.setInt(4, totalVotes + 1);
                sql.setLong(5, now);
                sql.executeUpdate();
            }
            catch (SQLException e){
                plugin.getLogger().error("SQL Error", e);
            }
        }
        else {
            try(
                PreparedStatement sql = connection.getPreparedStatement(
                    String.format("INSERT INTO %s (IGN, votes, lastvoted) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE IGN=?, votes=?, lastvoted=?", tableTotal)
                );
                Connection connection = sql.getConnection();
            ) {
                sql.setString(1, player);
                sql.setInt(2, totalVotes);
                sql.setLong(3, now);
                sql.setString(4, player);
                sql.setInt(5, totalVotes + 1);
                sql.setLong(6, now);
                sql.executeUpdate();
            }
            catch (SQLException e){
                plugin.getLogger().error("SQL Error", e);
            }
        }
    }

    public void offline(String player, String serviceName, String timeStamp, String address){
        try(
            PreparedStatement sql = connection.getPreparedStatement(String.format("INSERT INTO %s VALUES (?, ?, ?, ?)", tableQueue));
            Connection connection = sql.getConnection();
        ){
            sql.setString(1, player);
            sql.setString(2, serviceName);
            sql.setString(3, timeStamp);
            sql.setString(4, address);
            sql.executeUpdate();
        }
        catch(SQLException e){
            plugin.getLogger().error("SQL Error", e);
        }
    }

    public boolean queueUsername(String player){
        try(
            PreparedStatement sql = connection.getPreparedStatement(String.format("SELECT * FROM %s WHERE IGN=?" , tableQueue));
            Connection connection = sql.getConnection();
        ){
            sql.setString(1, player);
            try(ResultSet resultSet = sql.executeQuery()){
                if(!resultSet.next()){
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL Error", e);
            return false;
        }
    }

    public List<String> queueReward(String player){
        List<String> service = new ArrayList<String>();

        try(
            PreparedStatement sql = connection.getPreparedStatement(String.format("SELECT service FROM %s WHERE IGN=?", tableQueue));
            Connection connection = sql.getConnection();
        ){
            sql.setString(1, player);
            ResultSet resultSet = sql.executeQuery();
            while (resultSet.next()){
                service.add(resultSet.getString("service"));
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL Error", e);
        }
        return service;
    }

    public void removeQueue(String player, String service){
        try(
            PreparedStatement sql = connection.getPreparedStatement(String.format("DELETE FROM %s WHERE IGN=? and service=?", tableQueue));
            Connection connection = sql.getConnection();
        ){
            sql.setString(1, player);
            sql.setString(2, service);
            sql.executeUpdate();
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL Error", e);
        }
    }

    public List<String> queueAllPlayer(){
        List<String> player = new ArrayList<String>();
        try(
            PreparedStatement sql = connection.getPreparedStatement(String.format("SELECT IGN FROM `%s`", tableQueue));
            Connection connection = sql.getConnection();
        ){
            ResultSet resultSet = sql.executeQuery();
            while(resultSet.next()) {
                player.add(resultSet.getString("IGN"));
            }
        }
        catch (SQLException e){
            plugin.getLogger().error("SQL Error", e);
        }
        return player;
    }
}
