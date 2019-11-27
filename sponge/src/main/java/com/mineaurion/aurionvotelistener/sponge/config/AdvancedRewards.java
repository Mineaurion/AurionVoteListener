package com.mineaurion.aurionvotelistener.sponge.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class AdvancedRewards{

    @Setting
    public Map<Integer, ExtraServices> extraReward = ImmutableMap.of(10, new ExtraServices());

    @Setting
    public Map<Integer, ExtraServices> cumulativeVoting = ImmutableMap.of(5, new ExtraServices());

    @Setting
    public Map<String, ExtraServices> perms = ImmutableMap.of("vote.permission", new ExtraServices());

    @ConfigSerializable
    public static class ExtraServices {
        @Setting
        public String broadcast;
        @Setting
        public String playerMessage;
        @Setting
        public List<String> commands = ImmutableList.of();

    }

}
