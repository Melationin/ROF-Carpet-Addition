package com.carpet.rof.rules.getBiomeLayerCache;

import carpet.api.settings.Rule;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class BiomeLayerCacheSetting extends BaseSetting {
    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "0", "3" },
            strict = false
    )
    public static int getBiomeLayerCache = 0;
}
