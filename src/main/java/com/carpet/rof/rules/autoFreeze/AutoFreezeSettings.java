package com.carpet.rof.rules.autoFreeze;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;


import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.api.settings.RuleCategory.FEATURE;

@ROFRule
public class AutoFreezeSettings extends BaseSetting
{
    @Rule(categories = {ROF, FEATURE},
          strict = false,
          options = {"-1", "500", "1000"})
    public static int highLagFreezeLimit = -1;


    @Rule(categories = {ROF, FEATURE},
          strict = false,
          options = {"3", "5", "10"})
    public static int highLagFreezeTickLimit = 3;

    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    public static String commandUnfreeze = "false";


}
