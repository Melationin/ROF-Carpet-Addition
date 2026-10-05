package com.carpet.rof.entity.drops;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class DropSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL})
    @QuickTranslations(
            name = "装备掉落优化",
            description = "跳过生物死亡时空装备槽的掉落附魔计算，以及没有装备掉落效果的附魔上下文创建。"
    )
    public static boolean optimizedEquipmentDrops = false;

    @Rule(categories = {ROF, OPTIMIZATION})
    @QuickTranslations(
            name = "经验球生成合并查询优化",
            description = "生成经验球时找到第一个符合原版合并条件的经验球就停止查询。"
    )
    public static boolean optimizedExperienceOrbMerge = false;
}
