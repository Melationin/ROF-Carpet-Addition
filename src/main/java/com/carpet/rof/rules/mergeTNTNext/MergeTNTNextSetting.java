package com.carpet.rof.rules.mergeTNTNext;

import carpet.api.settings.Rule;
import com.carpet.rof.BaseSetting;
import com.carpet.rof.RulesSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import static carpet.api.settings.RuleCategory.TNT;

@RulesSetting
public class MergeTNTNextSetting extends BaseSetting {
    @Rule(
            categories = {ROF,OPTIMIZATION,TNT}
    )
    public static boolean mergeTNTNext = false;


}
