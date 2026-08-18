package com.carpet.rof.rules.fakePlayerTick;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class FakePlayerTickSettings extends BaseSetting {

    @Rule(
            categories = {ROF, OPTIMIZATION},
            strict = false
    )
    @QuickTranslations(
            name = "假人tick精简",
            description = "精简Carpet假人的客户端同步/进度/统计/Waypoint等每tick逻辑，保留主手物品tick与行为模拟",
            extra = {
                    "仅对Carpet假人(EntityPlayerMPFake)生效",
                    "可能影响假人的统计、进度、计分板自动同步与定位条显示"
            }
    )
    public static boolean optimizedFakePlayerTick = false;
}
