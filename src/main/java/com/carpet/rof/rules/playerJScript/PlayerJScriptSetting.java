package com.carpet.rof.rules.playerJScript;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.RulesSetting;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.BaseSetting.ROF;

@RulesSetting
public class PlayerJScriptSetting {

    @Rule(
            categories = {ROF,COMMAND,EXPERIMENTAL},
            strict = false,
    validators ={
        Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "假人脚本命令",
            description = "假人脚本执行命令权限等级",
            extra = {"使用时，将命名为#script的书与笔放入假人背包后，使用/playerJScript ... start执行",
            "使用/playerJScript ... stop 或者报错时停止。",
                    "报错停止时会丢出所以有错误的书，并尝试向编写者报错（如果有）"
            }
    )
    public static String  commandPlayerJScript = "false";

}
