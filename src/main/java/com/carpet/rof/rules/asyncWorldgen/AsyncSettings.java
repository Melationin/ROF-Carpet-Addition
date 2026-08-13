package com.carpet.rof.rules.asyncWorldgen;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;


@ROFRule
public class AsyncSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL})
    @QuickTranslations(name = "随机刻区块延迟缓存",
                       description = "每40gt刷新一次随机刻区块列表。关闭时完全使用原版遍历。")
    public static boolean randomTickChunkCache = false;

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL})
    @QuickTranslations(name = "生物生成区块延迟缓存",
                       description = "每40gt刷新一次自然生成候选区块列表。关闭时完全使用原版遍历。")
    public static boolean spawningChunkCache = false;

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL})
    @QuickTranslations(name = "随机刻异步",
                       description = "异步预计算下一游戏刻的随机刻候选；结果失效时自动回退原版。",
                       extra = "需要 Lithium。")
    public static boolean asyncRandomTick = false;

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL})
    @QuickTranslations(name = "生物生成异步",
                       description = "异步预计算自然生成候选；实体创建和数量统计仍在主线程执行。",
                       extra = "数据不可用或结果过期时自动回退原版。")
    public static boolean asyncNaturalSpawning = false;
}
