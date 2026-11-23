package com.carpet.rof.world.blockEntityIterationOptimization;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class BlockEntityIterationOptimizationSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION})
    @QuickTranslations(
            name = "方块实体遍历优化",
            description = "遍历方块实体时用一次原地压缩代替逐个 remove，避免大量方块实体被移除时的二次复杂度开销。",
            extra = {
                    "只改变遍历与移除方式，方块实体的 tick 顺序与判定条件与原版一致",
                    "被移除的方块实体在本次遍历结束后统一清理"
            })
    public static boolean blockEntityIterationOptimization = false;
}
