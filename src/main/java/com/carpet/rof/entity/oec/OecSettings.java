package com.carpet.rof.entity.oec;

import carpet.api.settings.Rule;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.carpet.BaseSetting;

import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;
import net.minecraft.commands.CommandSourceStack;

@ROFRule
public class OecSettings extends BaseSetting {
    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL}, validators = ToggleValidator.class)
    @QuickTranslations(name = "??????",
            description = "???????????????? 0 ????????????????????")
    public static boolean optimizedEntityCollision = false;

    @Rule(categories = {ROF, OPTIMIZATION, EXPERIMENTAL}, validators = ToggleValidator.class)
    @QuickTranslations(name = "????????",
            description = "? Entity ?????LivingEntity ??/???Piglin ????????????? SynchedEntityData ???")
    public static boolean optimizedSynchedData = false;
    public static final class ToggleValidator extends Validator<Boolean> {
        @Override
        public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> rule, Boolean value, String userInput) {
            return value;
        }
    }
}
