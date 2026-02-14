package com.carpet.rof.rules.netherPortalFix;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFRule
public class reverseBlockPosTraversal
{
    @Rule(
            categories = {ROF,FEATURE}
    )
    @QuickTranslations(
            name = "BlockPos Y轴遍历反向",
            description = "让BlockPos Y轴遍历反向，让实体传送地狱门优先传送靠上的地狱门"
    )
    public static boolean reverseBlockPosTraversal = false;

}
