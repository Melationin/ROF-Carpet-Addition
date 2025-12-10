package com.carpet.rof;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.*;

/**
 * Here is your example Settings class you can plug to use carpetmod /carpet settings command
 */
@SuppressWarnings("CanBeFinal")
public class ROFCarpetSettings
{

    public static final String ROF = "RofROF";
    public static final String HCL = "RofHCL";
    public static final String PACKET = "Packet";


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


    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "0", "10" },
            strict = false
    )
    public static int mobAIDelay = 0;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "0", "3" },
            strict = false
    )
    public static int getBiomeLayerCache = 0;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE}

    )
    public static boolean optimizeSpawnAttempts = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE}

    )
    public static boolean optimizeItemMerge = false;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "0", "20" },
            strict = false
    )
    public static int piglinLootItemDelay = 0;

    @Rule(
            categories = {ROF,OPTIMIZATION,FEATURE},
            options = { "100", "10000" },
            strict = false
    )
    public static int piglinMax= 10000;


    @Rule(
            categories = {ROF,OPTIMIZATION,PACKET},
            strict = false
    )
    public static boolean TntPacketOptimization;
}
