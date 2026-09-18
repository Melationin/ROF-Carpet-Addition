package com.carpet.rof.rules.enderPearl;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.*;


@ROFRule
public class EnderPearlSettings extends BaseSetting {

    @Rule(
            options = { "16.0", "-1.0" },
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false
    )@QuickTranslations(
            name = "更好的高速珍珠自加载",
            description = "(已不建议，更好的珍珠加载票可以平替，而且效果更好)对速度高于一定值的珍珠使用新的加载逻辑，更加稳定，需要加载的区块更少。",
            extra = {"设置的值表示自加载速度阈值。设置为负值时，表示禁用。",
                "对于新加载逻辑的珍珠，其加载逻辑与原版有较大差异。"
            }
    )
    public static double enderPearlForcedTickMinSpeed = -1;

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            options = {"false","true"},
            strict = true
    )
    @QuickTranslations(
            name = "优化珍珠tick",
            description = "让大多数情况下高速珍珠的飞行不生成新区块，可大幅度减少存档体积。在ECM未打开时，只会让世界高度外的珍珠不生成区块",
            extra = {"已知特性：珍珠会忽略未加载的实体碰撞箱。"}
    )
    public static boolean optimizedEnderPearlTick = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL},
            options = {"false","true"},
            strict = true
    )
    @QuickTranslations(

            name = "更好的珍珠加载票",
            description = "用一种特殊的加载票替代某些情况下原有的加载票。在ECM未打开时，只对世界高度外的珍珠有效"
    )
    public static boolean betterEnderPearlTicket = false;


    @Rule(
            options = {"0", "50","100","1000"},
            categories = {ROF,OPTIMIZATION,FEATURE}
    )@QuickTranslations(
            name = "珍珠加载堵塞主线程",
            description = "让珍珠的区块加载堵塞主线程，减少珍珠tick和世界tick不同步的问题。",
            extra = {"此处为最大堵塞时间，设置为0表示禁用。"}
    )
    public static int blockingEnderPearlLoading = 0;

}
