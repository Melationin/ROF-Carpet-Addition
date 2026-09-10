package com.carpet.rof.rules.oec;

import carpet.api.settings.Rule;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.BaseSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import net.minecraft.commands.CommandSourceStack;

@ROFRule
public class OecSettings extends BaseSetting {
    @Rule(categories = {ROF, OPTIMIZATION,EXPERIMENTAL}, validators = ToggleValidator.class)
    @QuickTranslations(name = "实体推挤收集优化", description = "使用分区空间索引优化 Lithium 的高密度实体推挤候选收集。可能改变实体遍历顺序。")
    public static boolean optimizedEntityCollection = false;

    public static final class ToggleValidator extends Validator<Boolean> {
        @Override
        public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> rule, Boolean value, String userInput) {
            if (!value) OecGridRegistry.releaseAll();
            return value;
        }
    }
}
