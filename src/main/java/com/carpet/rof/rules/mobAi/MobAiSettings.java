package com.carpet.rof.rules.mobAi;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;
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

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"0", "0.1"}, strict = false, validators = Validators.Probablity.class)
    @QuickTranslations(
            name = "生物AI优化概率",
            description = "生物AI优化实体 进行ai优化的概率",
            extra = {
                    "建议取值：0（默认，功能关闭）、0.1（规则本身不限制取值，0~1 之间任意值都可以填）",
                    "概率为 0，或实体列表为空时，功能完全不生效",
                    "掷骰结果随实体存档保存（实体 NBT 的 MobAi 字段），重载区块不会重掷"
            }
    )
    public static double mobAiChance = 0.0D;

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"!minecraft:drowned", "!minecraft:drowned,!minecraft:piglin"},
            validators = MobAiEntityListValidator.class, strict = false)
    @QuickTranslations(
            name = "生物AI优化实体列表",
            description = "需要参与 AI 优化的实体列表，逗号分隔；条目可以是实体 ID（minecraft:pig）或实体类型标签（#zombies），前缀 ! 表示排除该项。列表全为排除项时表示「除这些之外的全部」；列表为空时功能不生效。",
            extra = {
                    "建议取值：!minecraft:drowned（默认，除溺尸以外的全部生物）、!minecraft:drowned,!minecraft:piglin（再排除猪灵）",
                    "示例：minecraft:pig,#zombies,!#undead —— 猪与僵尸类生物，但不包括带 undead 标签的",
                    "匹配语义：先取所有正选条目的并集，再减去所有负选条目；命中任意一个正选条目即可",
                    "写错实体 ID 时该次设置会被拒绝并保持原值"
            })
    public static String mobAiEntities = "!minecraft:drowned";

    @Rule(categories = {ROF, OPTIMIZATION}, options = {"50", "200"}, strict = false, validators = RestoreTicksValidator.class)
    @QuickTranslations(
            name = "生物AI恢复时间",
            description = "被关闭 AI 的实体经过多少 tick 后恢复 AI，必须是正数。",
            extra = {
                    "建议取值：50（默认，2.5 秒）、200（10 秒）",
                    "剩余时间随实体存档保存；小于 0 表示「已判定且不关闭 AI」",
                    "必须是正数"
            })
    public static int mobAiRestoreTicks = 50;

    public static MobAiFilter mobAiFilter = MobAiFilter.EMPTY;

    public static boolean enabled()
    {
        return mobAiChance > 0.0D && !mobAiFilter.isEmpty();
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
