package com.carpet.rof.rules.highChunkListener;

import carpet.api.settings.Rule;
import com.carpet.rof.BaseSetting;
import com.carpet.rof.RulesSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@RulesSetting
public class HighChunkListenerSetting extends BaseSetting {

    @Rule(
            categories = {ROF,HCL,EXPERIMENTAL}
    )
    public static boolean highChunkListener = true;

    @Rule(
            categories = {ROF,HCL,OPTIMIZATION,EXPERIMENTAL}
    )
    public static boolean optimizeRaycastWithHCL = false;
}
