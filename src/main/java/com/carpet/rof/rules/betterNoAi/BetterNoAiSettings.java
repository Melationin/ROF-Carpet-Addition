package com.carpet.rof.rules.betterNoAi;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.FEATURE;

@ROFRule
public class BetterNoAiSettings extends BaseSetting
{
    @Rule(categories = {ROF, FEATURE})
    @QuickTranslations(
            name = "更好的NoAI NBT",
            description = "实体带有 NoBrainAI NBT 时跳过其 AI 逻辑（Mob.serverAiStep），但保留重力、流体流动、实体推挤、挤压伤害、爆炸击退、活塞推动与骑乘等被动运动。与原版 NoAI 不同，实体不会悬空静止。",
            extra = {
                    "用法：/data merge entity <目标> {NoBrainAI:1b} 设置，{NoBrainAI:0b} 或 /data remove entity <目标> NoBrainAI 移除；标签随实体存档保存，也支持 /summon 时直接写入",
                    "被跳过的：目标选择器、goal 选择器、寻路导航、传感器(sensing)、大脑(brain)、移动/视角/跳跃控制器",
                    "保留的：重力与落地摩擦、水流/气泡柱、实体互推与挤压伤害、爆炸击退、活塞推动、骑乘（玩家仍可操控坐骑）、燃烧、捡装备、距离消失检查",
                    "注意：与 AI 无关的 noActionTime 不再累加，因此在玩家附近也不会因 600 tick 无动作而消失（与原版 NoAI 行为一致）；末影龙/凋灵等 Boss 若打上该标签将停止相位推进"
            })
    public static boolean betterNoAiNbt = false;
}
