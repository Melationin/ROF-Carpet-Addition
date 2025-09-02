package com.carpet.rof;

import carpet.api.settings.Rule;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import static  carpet.api.settings.RuleCategory.*;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Here is your example Settings class you can plug to use carpetmod /carpet settings command
 */
public class ROFCarpetSettings
{

    public static final String ROF = "RofROF";
    public static final String HCL = "RofHCL";

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
            categories = {ROF,HCL,EXPERIMENTAL}

    )
    public static boolean highChunkListener = true;

    @Rule(
            categories = {ROF,HCL,OPTIMIZATION,EXPERIMENTAL}
    )
    public static boolean optimizeRaycastWithHCL = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,TNT}
    )
    public static boolean mergeTNTNext = false;


}
