package com.mineaurion.aurionvotelistener.sponge;

import com.mineaurion.aurionvotelistener.sponge.config.AdvancedRewards;
import com.mineaurion.aurionvotelistener.sponge.config.Rewards;

import java.util.List;

public class ServiceRewardConfig {
    public List<String> commands;
    public String broadcast;
    public String playerMessage;
    public String serviceName;

    public ServiceRewardConfig(AdvancedRewards.ExtraServices extraServices){
        this.commands = extraServices.commands;
        this.broadcast = extraServices.broadcast;
        this.playerMessage = extraServices.playerMessage;
        this.serviceName = "";
    }

    public ServiceRewardConfig(Rewards.Services services, String serviceName){
        this.commands = services.commands;
        this.broadcast = services.broadcast;
        this.playerMessage = services.playerMessage;
        this.serviceName = serviceName;
    }
}
