package com.carpet.rof.carpet;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;

import static com.carpet.rof.carpet.BaseSetting.ROF;

@ROFRule
public class FakePlayerFixSetting
{
    @Rule(
            categories = {ROF, RuleCategory.BUGFIX},
            strict = false
    )
    @QuickTranslations(
            name = "GCA 假人生成保留载具",
            description = "GCA 的 /bot load 与假人驻留通过 BotSpawnUtil.spawnBot 重新生成假人时，如果已从 RootVehicle 恢复载具，则跳过 teleportTo，避免假人下矿车"
    )
    public static boolean gcaBotSpawnKeepVehicle = false;
}
