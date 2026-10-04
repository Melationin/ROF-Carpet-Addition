package com.carpet.rof.rules.advancement;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;

@ROFRule
public class AdvancementSettings extends BaseSetting
{
    @Rule(categories = {ROF, OPTIMIZATION, FEATURE}, options = {"0", "1", "10", "100"},
            strict = false, validators = TriggerLimitValidator.class)
    @QuickTranslations(
            name = "每游戏刻击杀进度触发次数上限",
            description = "限制每 tick 内 KilledTrigger.trigger 的执行次数",
            extra = "0表示无限制"
    )
    public static int killedTriggerLimitPerTick = 0;

    public static final class TriggerLimitValidator extends Validator<Integer>
    {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput)
        {
            return value != null && value >= 0 ? value : null;
        }

        @Override
        public String description()
        {
            return "必须是非负整数";
        }
    }
}
