package com.carpet.rof.entity.mobAi.betterNoAi;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.FEATURE;

@ROFRule
public class BetterNoAiSettings extends BaseSetting
{
    @Rule(categories = {ROF, FEATURE})
    @QuickTranslations(
            name = "更好的NoAI NBT",
            description = "实体带有 NoBrainAI NBT 时跳过其 AI 逻辑（Mob.serverAiStep",
            extra = {
                    "NoBrainAI:1b"
            })
    public static boolean betterNoAiNbt = false;
}
