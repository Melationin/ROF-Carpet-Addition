package com.carpet.rof.packetRules;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class PacketRulesSettings extends BaseSetting {

    @Rule(categories = {ROF, OPTIMIZATION, PACKET},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "发包限制", description = "发包限制总开关；具体参数通过 /carpet group packetLimits 管理并保存到 JSON")
    public static boolean packetLimits = false;

    // 旧名称：entitySpawnPacketLimitTicks
    @Rule(categories = {"SubRule:packetLimits"}, strict = false, options = {"-1", "256", "512", "1024"})
    @QuickTranslations(name = "每 tick 实体生成阈值", description = "同一世界、同一 tick 内同种实体超过此数量时，缩小后续实体的追踪范围",
            extra = {"需要开启发包限制总开关；负数禁用每 tick 限制"})
    public static int entitySpawnLimitPerTick = 512;

    // 旧名称：entitySpawnPacketLimitTicksTrackerDistance
    @Rule(categories = {"SubRule:packetLimits"}, strict = false, options = {"2", "16", "64"},
            validators = Validators.NonNegativeNumber.class)
    @QuickTranslations(name = "受限实体追踪范围", description = "超过每 tick 阈值的实体使用此追踪范围，单位为方块",
            extra = {"需要开启发包限制总开关"})
    public static int limitedEntityTrackingRange = 16;

    // 旧名称：entitySpawnPacketLimitSeconds
    @Rule(categories = {"SubRule:packetLimits"}, strict = false, options = {"-1", "128", "256", "512"})
    @QuickTranslations(name = "每秒实体生成阈值", description = "根据同一区块上一秒同种实体的生成数量，按概率阻止后续实体的追踪与发包",
            extra = {"需要开启发包限制总开关；负数禁用每秒限制；一秒按 20 个游戏刻计算"})
    public static int entitySpawnLimitPerSecond = 256;

    // 旧名称：entitySpawnPacketLimitSecondsRecoverTime
    @Rule(categories = {"SubRule:packetLimits"}, strict = false, options = {"-1", "100", "200", "400"})
    @QuickTranslations(name = "实体追踪恢复时间", description = "受每 tick 或每秒限制的实体在存活时间达到此游戏刻数后，恢复限制前的追踪范围",
            extra = {"负数禁用自动恢复；关闭总开关仍会恢复受限实体"})
    public static int entityTrackingRecoveryTicks = 200;

    // 旧名称：particlesPacketsRange
    @Rule(categories = {"SubRule:packetLimits"}, strict = false, options = {"2.0", "8.0", "32.0"},
            validators = Validators.NonNegativeNumber.class)
    @QuickTranslations(name = "普通粒子发包范围", description = "限制非强制粒子包的发送距离，单位为方块",
            extra = {"需要开启发包限制总开关；关闭时使用原版 32 方块范围", "强制粒子仍使用原版 512 方块范围"})
    public static double particlePacketRange = 2.0;
}
