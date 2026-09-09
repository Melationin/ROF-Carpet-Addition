package com.carpet.rof.rules.oec;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class ClimbableTagCacheSettings extends BaseSetting {
    @Rule(categories = {ROF, OPTIMIZATION})
    @QuickTranslations(
            name = "可攀爬方块标签判断缓存",
            description = "缓存 Lithium 实体推挤判断中的 CLIMBABLE 方块标签查询，减少 LivingEntity.onClimbable 的重复标签查找。数据包重载后自动失效缓存。"
    )
    public static boolean optimizedClimbableTagCheck = false;
}
