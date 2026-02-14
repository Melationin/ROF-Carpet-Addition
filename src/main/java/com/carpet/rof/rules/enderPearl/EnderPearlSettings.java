package com.carpet.rof.rules.enderPearl;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import com.carpet.rof.rules.BaseSetting;
import com.carpet.rof.annotation.ROFRule;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.FEATURE;
import static carpet.api.settings.RuleCategory.OPTIMIZATION;


@ROFRule
public class EnderPearlSettings extends BaseSetting {
    public static class enderPearlForcedTickMinSpeedChance extends Validator<Double> {
        @Override
        public Double validate(ServerCommandSource source, CarpetRule<Double> currentRule, Double newValue, String string) {
            return  newValue;
        }

        @Override
        public String description() { return "";}
        /// ITEMS.
    }

    @Rule(

            options = { "16.0", "-1.0" },
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false,
            validators = enderPearlForcedTickMinSpeedChance.class

    )
    public static double enderPearlForcedTickMinSpeed = -1;

    @Rule(

            categories = {ROF,FEATURE}
    )
    public static boolean forceEnderPearlLogger = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            strict = false
    )
    public static boolean highEnderPearlNoChunkLoading = false;
}
