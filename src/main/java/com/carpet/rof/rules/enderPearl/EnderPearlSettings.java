package com.carpet.rof.rules.enderPearl;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.*;


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

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            options = {"false","true","1_21_2-","1_21_2+"},
            strict = true
    )
    @QuickTranslations(
            name = "优化自加载态珍珠tick",
            description = "仅在更好的珍珠自加载启用时可用。让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块",
            extra = {"已知特性：珍珠会忽略未加载的实体碰撞箱。",
                    "false - 关闭优化",
                    "true - 开启优化,且珍珠特性符合当前版本",
                    "1_21_2- - 开启优化,且珍珠特性符合1.21.2及以下版本",
                    "1_21_2+ - 开启优化,且珍珠特性符合1.21.2以上版本"}
    )
    public static String optimizeForcedEnderPearlTick = "false";


    @Rule(
            options = {"false", "true"},
            categories = {ROF,OPTIMIZATION,FEATURE}
    )@QuickTranslations(
            name = "珍珠加载强行同步",
            description = "防止珍珠加载时因为异步区块加载延迟而导致珍珠tick和世界tick不同步"
    )
    public static boolean enderPearlForcedSync = false;

}
