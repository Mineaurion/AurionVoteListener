package com.mineaurion.aurionvotelistener.sponge.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdvancedRewards extends PluginConfig{

    public AdvancedRewards(String directory) throws IOException {
        super(directory, "advanced-reward.conf");
    }

    @Setting
    public Map<Integer, ExtraReward> extraReward = ImmutableMap.of();
    @ConfigSerializable
    public class ExtraReward{
        @Setting
        public String broadcast;
        @Setting
        public String playerMessage;
        @Setting
        public List<String> commands = ImmutableList.of();
    }

    @Setting
    public Map<Integer, CumulativeVoting> cumulativeVoting = ImmutableMap.of();
    @ConfigSerializable
    public class CumulativeVoting{
        @Setting
        public String broadcast;
        @Setting
        public String playerMessage;
        @Setting
        public List<String> commands = ImmutableList.of();
    }

    @Setting
    public Map<String, Perms> perms = ImmutableMap.of();
    @ConfigSerializable
    public class Perms{
        @Setting
        public List<String> commands = ImmutableList.of();
    }
}
