package com.carpet.rof.rules.packerRules;

import carpet.api.settings.Rule;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class PacketRulesSettings extends BaseSetting {

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false
    )
    @QuickTranslations(
            name = "tnt实体发包优化",
            description = "通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体",
            extra = {"可能会造成客户端显示错误"}
    )
    public static boolean tntPacketOptimization = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false,
            options = {"-1","100","1000"}
    )
    @QuickTranslations(
            name = "每游戏刻实体生成发包限制",
            description = "在同一tick生成过多的同种实体时，减少过多的实体的发包距离。用于大当量珍珠炮的优化",
            extra = {"设置为负数表示禁用"}
    )
    public static int entitySpawnPacketLimitTicks = -1;

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false,
            options = {"2","16","64"}
    )
    @QuickTranslations(
            name = "限制态实体追踪距离",
            description = "设置发包限制的实体的发包距离"
    )
    public static int limitedEntityTrackerDistance = 16;

    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false,
            options = {"-1","100"}
    )
    @QuickTranslations(
            name = "每秒实体生成发包限制",
            description = "在同一秒生成过多的同种实体时，取消当前区块同种实体对玩家的广播。",
            extra = {"设置为负数表示禁用"}
    )
    public static int entitySpawnPacketLimitSeconds = -1;
}
