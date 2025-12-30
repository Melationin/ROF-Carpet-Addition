package com.carpet.rof.rules.autoFreeze;

import carpet.api.settings.Rule;
import com.carpet.rof.BaseSetting;
import com.carpet.rof.RulesSetting;


import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.api.settings.RuleCategory.FEATURE;

@RulesSetting
public class AutoFreezeSettings extends BaseSetting{
    @Rule(
            categories = {ROF,FEATURE},
            strict = false,
            options = { "-1", "500","1000" }
    )
    public static int highLagFreezeLimit = -1;


    @Rule(
            categories = {ROF,FEATURE},
            strict = false,
            options = { "3", "5","10" }
    )
    public static int highLagFreezeTickLimit = 3;

    @Rule(
            categories = {ROF,COMMAND},
            strict = false
    )
    public static boolean unfreezeCommand = false;
}
