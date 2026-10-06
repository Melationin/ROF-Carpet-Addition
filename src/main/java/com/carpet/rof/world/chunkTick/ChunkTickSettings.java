package com.carpet.rof.world.chunkTick;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import com.carpet.rof.world.chunkTick.spawnStatistic.SpawnStatisticSimplifyValidator;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;


@ROFRule
public class ChunkTickSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "随机刻优化", description = "随机刻优化总开关；具体参数通过 /carpet group randomTickOptimizations 管理并保存到 JSON")
    public static boolean randomTickOptimizations = false;

    @Rule(categories = {"SubRule:randomTickOptimizations"})
    @QuickTranslations(name = "随机刻区块延迟缓存",
                       description = "每40gt刷新一次随机刻区块列表。关闭时完全使用原版遍历。")
    public static boolean randomTickChunkCache = false;

    @Rule(categories = {"SubRule:randomTickOptimizations"})
    @QuickTranslations(name = "随机刻异步",
                       description = "异步预计算下一游戏刻的随机刻候选；结果失效时自动回退原版。")
    public static boolean asyncRandomTick = false;

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "生物生成优化", description = "生物生成优化总开关；具体参数通过 /carpet group naturalSpawningOptimizations 管理并保存到 JSON")
    public static boolean naturalSpawningOptimizations = false;

    @Rule(categories = {"SubRule:naturalSpawningOptimizations"})
    @QuickTranslations(name = "生物生成区块延迟缓存",
                       description = "每40gt刷新一次自然生成候选区块列表。关闭时完全使用原版遍历。")
    public static boolean spawningChunkCache = false;

    @Rule(categories = {"SubRule:naturalSpawningOptimizations"})
    @QuickTranslations(name = "生物生成异步",
                       description = "异步预计算自然生成候选；实体创建和数量统计仍在主线程执行。",
                       extra = "数据不可用或结果过期时自动回退原版。")
    public static boolean asyncNaturalSpawning = false;

    @Rule(categories = {"SubRule:naturalSpawningOptimizations"},
            validators = SpawnStatisticSimplifyValidator.class, strict = false,
            options = {"{}", "{minecraft:overworld}", "{minecraft:overworld,minecraft:the_end}"})
    @QuickTranslations(name = "刷怪统计化简白名单",
                       description = "刷怪统计被化简的世界白名单，花括号包裹、逗号分隔的维度 ID；{} 表示任何世界都不化简。",
                       extra = {
                               "化简会跳过刷怪统计中的区块查找与生物群系查询",
                               "只有世界中任何生物群系都没有刷怪密度限制时，化简的结果才与原版完全一致"
                       })
    public static String spawnStatisticSimplifyWhitelist = "{}";
}
