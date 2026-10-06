package com.carpet.rof.entity.mobAi;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import static carpet.api.settings.RuleCategory.FEATURE;

@ROFRule
public class MobAiSettings extends BaseSetting
{
    public static final String NBT_KEY = "MobAi";
    // 未命中列表时保持此值，不写入 NBT。
    public static final int UNDECIDED = Integer.MIN_VALUE;
    // 保存该值，避免重载后重新判定。
    public static final int KEEP_AI = -1;

    @Rule(categories = {ROF, OPTIMIZATION, FEATURE},
            validators = CompositeRuleValidator.class)
    @QuickTranslations(name = "生物 AI 优化", description = "生物 AI 优化总开关；具体参数通过 /carpet group mobAiOptimizations 管理并保存到 JSON")
    public static boolean mobAiOptimizations = false;

    @Rule(categories = {"SubRule:mobAiOptimizations"}, options = {"0", "0.5","0.9","0.95"}, strict = false, validators = Validators.Probablity.class)
    @QuickTranslations(
            name = "生物AI延迟概率",
            description = "白名单内的生物发生 AI 延迟、暂时停止 AI 行为的概率。",
            extra = {
                    "概率为 0，或实体列表为空时，功能完全不生效",
                    "掷骰结果随实体存档保存（实体 NBT 的 MobAi 字段）"
            }
    )
    public static double mobAIDelayChance = 0.0D;

    @Rule(categories = {"SubRule:mobAiOptimizations"}, options = {"{}", "{!minecraft:drowned}", "{!minecraft:drowned,!minecraft:piglin}"},
            validators = MobAiEntityListValidator.class, strict = false)
    @QuickTranslations(
            name = "生物AI延迟白名单",
            description = "可能发生 AI 延迟的生物白名单，花括号包裹、逗号分隔；条目可以是实体 ID（minecraft:pig）或实体类型标签（#zombies），前缀 ! 表示排除该项。列表全为排除项时表示「除这些之外的全部」；{} 表示空列表，功能不生效。",
            extra = {
                    "建议取值：{!minecraft:drowned}（默认，除溺尸以外的全部生物）、{!minecraft:drowned,!minecraft:piglin}（再排除猪灵）",
                    "示例：{minecraft:pig,#zombies,!#undead} —— 猪与僵尸类生物，但不包括带 undead 标签的",
                    "先取所有正选条目的并集，再减去所有负选条目；命中任意一个正选条目即可"
            })
    public static String mobAIDelayWhitelist = "{!minecraft:drowned}";

    @Rule(categories = {"SubRule:mobAiOptimizations"}, options = {"3","50", "200"}, strict = false, validators = RestoreTicksValidator.class)
    @QuickTranslations(
            name = "生物AI延迟时间",
            description = "生物 AI 延迟持续的 tick 数，结束后恢复 AI，必须是正数。",
            extra = {
                    "剩余时间随实体存档保存；小于 0 表示「已判定且不关闭 AI」",
                    "必须是正数"
            })
    public static int mobAIDelayTicks = 3;

    @Rule(categories = {"SubRule:mobAiOptimizations"})
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

    @Rule(categories = {"SubRule:mobAiOptimizations"}, options = {"0", "20"}, strict = false)
    @QuickTranslations(name = "猪灵捡掉落物延迟", description = "只有出现一定时间的掉落物才会被猪灵捡起")
    public static int piglinLootItemDelay = 0;

    @Rule(categories = {"SubRule:mobAiOptimizations"}, options = {"100", "10000"}, strict = false)
    @QuickTranslations(name = "堆叠猪灵AI抑制", description = "对于堆叠到一定量的猪灵，抑制其中部分猪灵的ai。")
    public static int piglinStackingAISuppression = 10000;

    public static MobAiFilter mobAiFilter = MobAiFilter.EMPTY;

    public static boolean enabled()
    {
        return mobAiOptimizations && mobAIDelayChance > 0.0D && !mobAiFilter.isEmpty();
    }

    public static final class RestoreTicksValidator extends Validator<Integer>
    {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer newValue, String userInput)
        {
            return newValue != null && newValue > 0 ? newValue : null;
        }

        @Override
        public String description()
        {
            return "必须是正数（tick）";
        }
    }
}
