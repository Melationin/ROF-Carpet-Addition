package com.carpet.rof.rules.enderPearl;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;


@ROFRule
public class EnderPearlSettings extends BaseSetting {

    @Rule(
            options = { "16.0", "-1.0" },
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false
    )@QuickTranslations(
            name = "更好的高速珍珠自加载",
            description = "对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。",
            extra = {"设置的值表示自加载速度阈值。设置为负值时，表示禁用。",
                "对于新加载逻辑的珍珠，其加载逻辑与原版有较大差异。"
            }
    )
    public static double enderPearlForcedTickMinSpeed = -1;

}
