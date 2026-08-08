package com.carpet.rof.rules.piglinRules;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class PiglinRulesSettings extends BaseSetting {
    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "0", "20" },
            strict = false
    )
    @QuickTranslations(
            name = "猪灵捡掉落物延迟",
            description = "只有出现一定时间的掉落物才会被猪灵捡起"
    )
    public static int piglinLootItemDelay = 0;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "1", "100", "10000" },
            strict = false
    )
    @QuickTranslations(
            name = "堆叠猪灵AI抑制",
            description = "限制同方块堆叠猪灵的完整AI数量；其余猪灵仍可交易，并共享物品感知。"
    )
    public static int piglinStackingAISuppression = 10000;
}
