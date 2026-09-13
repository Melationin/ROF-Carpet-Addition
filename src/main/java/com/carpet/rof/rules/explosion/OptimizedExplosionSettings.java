package com.carpet.rof.rules.explosion;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class OptimizedExplosionSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL}, strict = false, options = {"0", "4", "8", "16"})
    @QuickTranslations(
            name = "爆炸优化",
            description = "同一游戏刻内同一点的连续爆炸达到该次数后，先按最坏情况判断这次爆炸是否可能破坏方块；只有不可能破坏方块时才跳过方块计算，只影响实体。",
            extra = {
                    "设置为 0 或负数表示禁用",
                    "只对同一游戏刻内、坐标与威力完全相同的连续爆炸生效"
            })
    public static int optimizedExplosion = 0;

    public static boolean enabled()
    {
        return optimizedExplosion > 0;
    }
}
