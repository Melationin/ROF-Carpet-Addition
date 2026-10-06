package com.carpet.rof.entity.mobAi;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class MobAiSettings extends BaseSetting
{
    public static final String NBT_KEY = "MobAi";
    // 未命中列表时保持此值，不写入 NBT。
    public static final int UNDECIDED = Integer.MIN_VALUE;
    // 保存该值，避免重载后重新判定。
    public static final int KEEP_AI = -1;

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"0", "0.5","0.9","0.95"}, strict = false, validators = Validators.Probablity.class)
    @QuickTranslations(
            name = "生物AI延迟概率",
            description = "白名单内的生物发生 AI 延迟、暂时停止 AI 行为的概率。"
    )
    public static double mobAIDelayChance = 0.0D;

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"{}", "{!minecraft:drowned}", "{!minecraft:drowned,!minecraft:piglin}"},
            validators = MobAiEntityListValidator.class, strict = false)
    @QuickTranslations(
            name = "生物AI延迟白名单",
            description = "可能发生 AI 延迟的生物白名单，花括号包裹、逗号分隔；条目可以是实体 ID或实体类型标签（如#zombies），前缀 ! 排除"
    )
    public static String mobAIDelayWhitelist = "{!minecraft:drowned}";

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"3","50", "200"}, strict = false, validators = RestoreTicksValidator.class)
    @QuickTranslations(
            name = "生物AI延迟时间",
            description = "生物 AI 延迟持续的 tick 数，结束后恢复 AI，必须是正数。")
    public static int mobAIDelayTicks = 3;

    public static MobAiFilter mobAiFilter = MobAiFilter.EMPTY;

    public static boolean enabled()
    {
        return mobAIDelayChance > 0.0D && !mobAiFilter.isEmpty();
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
