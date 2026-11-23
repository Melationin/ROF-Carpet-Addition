package com.carpet.rof.world.blockEntity;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class BlockEntityTickerSettings extends BaseSetting
{
    //? if =26.1 {
    @Rule(categories = {ROF, OPTIMIZATION})
    @QuickTranslations(
            name = "方块实体移除优化",
            description = "用读写双下标从 tick 列表中批量移除失效的方块实体 ticker，保留其余 ticker 的 tick 顺序。")
    public static boolean optimizedBlockEntityRemoval = false;
    //?}
}
