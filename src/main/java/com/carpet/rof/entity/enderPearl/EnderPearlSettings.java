package com.carpet.rof.entity.enderPearl;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;

import static carpet.api.settings.RuleCategory.*;


@ROFRule
public class EnderPearlSettings extends BaseSetting {

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "珍珠优化", description = "珍珠、ECM 和 raycast 优化总开关；具体参数通过 /carpet group enderPearlOptimizations 管理并保存到 JSON")
    public static boolean enderPearlOptimizations = false;

    @Rule(
            options = { "16.0", "-1.0" },
            categories = {"SubRule:enderPearlOptimizations"},
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
            categories = {"SubRule:enderPearlOptimizations"},
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
            categories = {"SubRule:enderPearlOptimizations"},
            options = {"false","true"}
    )
    @QuickTranslations(

            name = "更好的珍珠加载票",
            description = "用一种特殊的加载票替代某些情况下原有的加载票。在ECM未打开时，只对世界高度外的珍珠有效"
    )
    public static boolean betterEnderPearlTicket = false;


    @Rule(
            options = {"0", "50","100","1000"},
            categories = {"SubRule:enderPearlOptimizations"},
            strict = false
    )@QuickTranslations(
            name = "珍珠加载堵塞主线程",
            description = "让珍珠的区块加载堵塞主线程，减少珍珠tick和世界tick不同步的问题。",
            extra = {"此处为最大堵塞时间，设置为0表示禁用。"}
    )
    public static int blockingEnderPearlLoading = 0;

    @Rule(categories = {"SubRule:enderPearlOptimizations"})
    @QuickTranslations(
            name = "超高度区块标记器(ECM)",
            description = "Raycast优化前置，可能会造成额外的存储空间(一般只会增加存档的0.1%以下)",
            extra = {"在第一次启用时，务必使用/exceedChunkMarker 加载一次"})
    public static boolean exceedChunkMarker = false;

    @Rule(categories = {"SubRule:enderPearlOptimizations"})
    @QuickTranslations(
            name = "raycast优化",
            description = "通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过",
            extra = "已知特性：投掷物会忽略一些特定位置的实体碰撞箱。")
    public static boolean optimizedRaycast = false;

}
