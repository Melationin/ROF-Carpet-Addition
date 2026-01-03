package com.carpet.rof.rules.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.RulesSetting;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.BaseSetting.ROF;

@RulesSetting
public class CommandSetting {
    @Rule(

            categories = {ROF,COMMAND},
            strict = false,
            validators ={Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "Carpet规则搜索命令",
            description = "添加了carpet的子命令search，可以通过关键字搜索carpet规则"
    )
    public static String commandRulesSearcher = "true";


}
