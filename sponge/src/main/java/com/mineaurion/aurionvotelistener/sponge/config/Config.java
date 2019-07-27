package com.mineaurion.aurionvotelistener.sponge.config;

import com.google.common.collect.ImmutableList;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class Config {

    @Setting(comment = "Database settings")
    public Database database = new Database();
    @ConfigSerializable
    public static class Database{
        @Setting
        public String storage = "sqlite";
        @Setting
        public String file = "sql.db";
        @Setting
        public String host = "";
        @Setting
        public String user = "";
        @Setting
        public String pass = "";
        @Setting
        public int port = 3306;
        @Setting(comment = "Database name")
        public String name = "Listener";
        @Setting
        public String prefix = "";
        @Setting
        public String tableTotal = "ListenerTotal";
        @Setting
        public String tableQueue = "ListenerQueue";
    }

    @Setting
    public Settings settings = new Settings();
    @ConfigSerializable
    public static class Settings{
        @Setting(comment = "Reward based on permission")
        public boolean permissionReward = false;
        @Setting(comment = "Reward based on chance, the number represente a percentage")
        public boolean chanceReward = false;
        @Setting(comment = "Reward based on x times the player has vote")
        public boolean cumulativeReward = false;
        @Setting(comment = "This settings will disable the queue for being process, usefull when you have nuvotifier doing this")
        public boolean queueVote = true;
        @Setting(comment = "When a player receive all vote in his queue, this settings affect only the message")
        public Offline offline = new Offline();
        @ConfigSerializable
        public static class Offline{
            @Setting
            public boolean enable = false;
            @Setting
            public String broadcast = "&a<player> voted <amt> times while they were offline and received rewards!";
            @Setting
            public String playermessage = "&aThanks for voting !";
        }
        @Setting(comment = "When a player receive a vote, this settings affect only the message")
        public Vote vote = new Vote();
        @ConfigSerializable
        public static class Vote{
            @Setting
            public boolean command = true;
            @Setting
            public List<String> message = ImmutableList.of(
                    "&6-----------------------------------------------------",
                    "Vote for us every day for in game rewards and extras",
                    "&6-----------------------------------------------------",
                    "&bYou currently have &a<votes> Votes"
            );
        }
        @Setting(comment = "When a player join, this settings affect only the message")
        public Join join = new Join();
        @ConfigSerializable
        public static class Join{
            @Setting
            public boolean enable = true;
            @Setting
            public List<String> message = ImmutableList.of(
                    "&6-----------------------------------------------------",
                    "Vote for us every day for in game rewards and extras",
                    "&6-----------------------------------------------------",
                    "&bYou currently have &2<votes> Votes"
            );
        }
        @Setting(comment = "Announcement message and delay configuration")
        public Announcement announcement = new Announcement();
        @ConfigSerializable
        public static class Announcement{
            @Setting
            public Integer delay = 300;
            @Setting
            public List<String> message = ImmutableList.of(
                    "&6-----------------------------------------------------",
                    "Vote for us every day for in game rewards and extras",
                    "&6-----------------------------------------------------"
            );
        }
        @Setting(comment = "VoteTop message")
        public VoteTop voteTop = new VoteTop();
        @ConfigSerializable
        public static class VoteTop{
            @Setting
            public Integer number = 10;
            @Setting
            public String format = "<POSITION>. &a<username> - &f<TOTAL>";
            @Setting
            public List<String> header = ImmutableList.of("&6---------------- &f( &3Top Voters&f ) &6----------------");
        }
    }
}
