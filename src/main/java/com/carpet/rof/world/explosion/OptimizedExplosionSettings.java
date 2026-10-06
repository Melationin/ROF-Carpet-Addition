package com.carpet.rof.world.explosion;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import com.carpet.rof.entity.merge.MergeSetting.MergeTNTNextMode;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import static carpet.api.settings.RuleCategory.TNT;

@ROFRule
public class OptimizedExplosionSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, TNT, EXPERIMENTAL},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "TNT 与爆炸优化", description = "TNT 与爆炸优化总开关；具体参数通过 /carpet group tntExplosionOptimizations 管理并保存到 JSON")
    public static boolean tntExplosionOptimizations = false;

    @Rule(categories = {"SubRule:tntExplosionOptimizations"})
    @QuickTranslations(
            name = "合并TNTnext",
            description = "更为激进的tnt合并方案, 可能会导致预期之外的结果。不能与其他tnt合并一起开。",
            extra = {"False:关闭合并",
                    "TRUE: 旧版的爆炸处理方案，会使移动中的合并tnt发生不原版的行为",
                    "SAFE: 更安全的爆炸处理方案，让移动中的合并tnt行为与原版一致",
                    "AlmostVanilla: 更接近原版的tnt合并方案，只在tnt爆炸时合并，并且保tnt tick顺序",
                    "SafePlus: 与 AlmostVanilla 一样只在tnt爆炸时合并，但合并判定放在每tick一次的提前遍历里，只合并遍历中相邻的同点位tnt，且不改写实体tick顺序",
                    "理论上，AlmostVanilla 模式不会改变 TNT 行为，因此在纯原版中不应发现相关差异。若出现与原版不一致的情况，请提交 issue。"
            })
    public static MergeTNTNextMode mergeTNTNext = MergeTNTNextMode.FALSE;

    @Rule(categories = {"SubRule:tntExplosionOptimizations"})
    @QuickTranslations(
            name = "爆炸合并",
            description = "把合并TNT产生的多次爆炸合成一次，只对实体作用一次。仅在合并TNT和爆炸优化已启用时生效。",
            extra = {"需要 mergeTNTNext 处于非 False 模式",
                    "伤害只结算一次；推力按合并次数补齐，总推力与原版一致"})
    public static boolean mergeExplosion = false;

    @Rule(categories = {"SubRule:tntExplosionOptimizations"}, strict = false, options = {"0", "4", "8", "16"})
    @QuickTranslations(
            name = "爆炸优化",
            description = "同一游戏刻内同一点的连续爆炸达到该次数后，先按最坏情况判断这次爆炸是否可能破坏方块；只有不可能破坏方块时才跳过方块计算，只影响实体。",
            extra = {
                    "设置为 0 或负数表示禁用",
                    "只对同一游戏刻内、坐标与威力完全相同的连续爆炸生效"
            })
    public static int optimizedExplosion = 0;

    @Rule(categories = {"SubRule:tntExplosionOptimizations"}, strict = false)
    @QuickTranslations(
            name = "tnt实体发包优化",
            description = "通过去掉不必要的tnt实体发包(Fuse)与减少发包频率，优化tnt实体",
            extra = {"可能会造成客户端显示错误"})
    public static boolean tntPacketOptimization = false;

    public static boolean enabled()
    {
        return tntExplosionOptimizations && optimizedExplosion > 0;
    }
}
