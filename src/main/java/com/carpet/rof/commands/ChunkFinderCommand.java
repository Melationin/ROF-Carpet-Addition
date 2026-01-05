package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.CarpetCommand;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.RulesSetting;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.BaseSetting.ROF;


public class ChunkFinderCommand {

    @Rule(

            categories = {ROF,COMMAND},
            strict = false,
            validators ={Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "记录加载区块",
            description = "记录当前tick或者一段时间内tick，加载的区块数量和加载区块联通区域的数量，用于查找被遗忘的区块加载器"
    )
    public static String commandSafeTick = "false";
}
