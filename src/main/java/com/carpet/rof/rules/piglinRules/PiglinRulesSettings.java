package com.carpet.rof.rules.piglinRules;

import carpet.api.settings.Rule;
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
    public static int piglinLootItemDelay = 0;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "100", "10000" },
            strict = false
    )
    public static int piglinMax= 10000;
}
