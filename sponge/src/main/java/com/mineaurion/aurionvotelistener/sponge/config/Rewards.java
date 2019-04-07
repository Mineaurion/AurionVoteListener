package com.mineaurion.aurionvotelistener.sponge.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

public class Rewards{

    @Setting
    public Map<String, Services> services = ImmutableMap.of("DEFAULT", new Services());

    @ConfigSerializable
    public static class Services{
        @Setting
        public String broadcast = "&a<username> &6has voted @ &a<service> &6and received &a5 Diamonds !";
        @Setting
        public String playerMessage = "&aThanks for voting at &b<service> !";
        @Setting
        public List<String> commands = ImmutableList.of(
                "give <username> minecraft:diamond 10",
                "give <username> minecraft:wool 1"
        );
    }

}
