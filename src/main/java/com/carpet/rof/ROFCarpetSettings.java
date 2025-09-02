package com.carpet.rof;

import carpet.api.settings.Rule;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Here is your example Settings class you can plug to use carpetmod /carpet settings command
 */
public class ROFCarpetSettings
{
    public enum ComparatorOptions {
        VANILLA,
        BEHIND,
        LENIENT,
        EXTENDED;
    }

    public static final String YCT = "YCT";
    public static final String HCL = "HCL";
    public static class enderPearlRaycastLengthChance extends Validator<Double> {
        @Override
        public Double validate(ServerCommandSource source, CarpetRule<Double> currentRule, Double newValue, String string) {
            return newValue > 0  ? newValue : null;
        }

        @Override
        public String description() { return "must >= 0";}
    }

    @Rule(
            options = { "8.0", "16.0", "32.0", "999999.0" },
            categories = {YCT},
            strict = false,
            validators = enderPearlRaycastLengthChance.class
    )

    public static double enderPearlRaycastLength = 8;

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
            categories = {YCT},
            strict = false,
            validators = enderPearlForcedTickMinSpeedChance.class
    )
    public static double enderPearlForcedTickMinSpeed = -1;

    @Rule(

            categories = {YCT}
    )
    public static boolean enderPearlSkipUngeneratedRegion = false;

    @Rule(

            categories = {YCT}
    )
    public static boolean forceEnderPearlLogger = false;

    public static class commonIntValidator extends Validator<Integer> {
        @Override
        public Integer validate(ServerCommandSource source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
            return  newValue;
        }

        @Override
        public String description() { return "";}
        /// ITEMS.
    }



    public static int ChunkUpdateHighInterval = 200;

    @Rule(
            categories = {YCT,HCL}

    )
    public static boolean highChunkListener = true;

    @Rule(
            categories = {YCT,HCL}
    )
    public static boolean optimizeRaycastWithHCL = false;

    @Rule(
            categories = {YCT,HCL}
    )
    public static boolean tntMergeNext = false;


}
