package com.carpet.rof.rules.optimizeItemMerge;

import carpet.api.settings.Rule;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class OptimizeItemMergeSetting extends BaseSetting {
    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE}

    )
    public static boolean optimizeItemMerge = false;
}
