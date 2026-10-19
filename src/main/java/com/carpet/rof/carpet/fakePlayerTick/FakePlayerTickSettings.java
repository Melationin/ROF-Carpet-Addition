package com.carpet.rof.carpet.fakePlayerTick;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class FakePlayerTickSettings extends BaseSetting {

    @Rule(
            categories = {ROF, OPTIMIZATION},
            strict = false
    )
    @QuickTranslations(
            name = "假人tick精简",
            description = "精简Carpet假人的进度/统计/计分板/容器同步/实体追踪/Waypoint等开销，保留行为模拟与掉落",
            extra = {
                    "仅对Carpet假人(EntityPlayerMPFake)生效",
                    "假人不再产生成就进度、统计与计分板数据，也不再被登记为实体的观察者"
            }
    )
    public static boolean optimizedFakePlayerTick = false;
}
