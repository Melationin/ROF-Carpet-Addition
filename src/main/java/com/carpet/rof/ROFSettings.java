package com.carpet.rof;

import com.carpet.rof.commands.PlayerJScriptCommand;
import com.carpet.rof.rules.autoFreeze.AutoFreezeSettings;
import com.carpet.rof.rules.commands.CommandSetting;
import com.carpet.rof.rules.enderPearl.EnderPearlSettings;
import com.carpet.rof.rules.entityPacketLimit.EntityPacketLimitSetting;
import com.carpet.rof.rules.getBiomeLayerCache.BiomeLayerCacheSetting;
import com.carpet.rof.rules.highChunkListener.HighChunkListenerSetting;
import com.carpet.rof.rules.mergeTNTNext.MergeTNTNextSetting;
import com.carpet.rof.rules.optimizeItemMerge.OptimizeItemMergeSetting;
import com.carpet.rof.rules.packerRules.PacketRulesSettings;
import com.carpet.rof.rules.piglinRules.PiglinRulesSettings;
import com.carpet.rof.rules.playerJScript.PlayerJScriptSetting;

import java.util.ArrayList;
import java.util.List;

public class ROFSettings {
    public static final  List<Class<?>> ruleClasses = new ArrayList<>();

    public static void register(){
       ruleClasses.add(PiglinRulesSettings.class);
       ruleClasses.add(PacketRulesSettings.class);
       ruleClasses.add(OptimizeItemMergeSetting.class);
       ruleClasses.add(MergeTNTNextSetting.class);
       ruleClasses.add(PacketRulesSettings.class);
       ruleClasses.add(HighChunkListenerSetting.class);
       ruleClasses.add(BiomeLayerCacheSetting.class);
       ruleClasses.add(EntityPacketLimitSetting.class);
       ruleClasses.add(EnderPearlSettings.class);
       ruleClasses.add(CommandSetting.class);
       ruleClasses.add(AutoFreezeSettings.class);
        ruleClasses.add(PlayerJScriptSetting.class);
    }

}
