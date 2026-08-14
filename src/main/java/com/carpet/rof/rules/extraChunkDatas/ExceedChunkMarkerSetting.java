package com.carpet.rof.rules.extraChunkDatas;

import carpet.api.settings.Rule;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class ExceedChunkMarkerSetting extends BaseSetting {

    @Rule(
            categories = {ROF,EXPERIMENTAL}
    )
    @QuickTranslations(
            name = "超高度区块标记器(ECM)",
            description = "Raycast优化前置，可能会造成额外的存储空间(一般只会增加存档的0.1%以下)",
            extra = "在第一次启用时，务必使用/exceedChunkMarker 加载一次"
    )
    public static boolean exceedChunkMarker = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,EXPERIMENTAL}
    )
    @QuickTranslations(
            name = "raycast优化",
            description = "通过ECM优化raycast，开启时请保证ECM已打开且已经从存档加载过",
            extra = "已知特性：投掷物会忽略一些特定位置的实体碰撞箱。"
    )
    public static boolean optimizeRaycast = false;


}
