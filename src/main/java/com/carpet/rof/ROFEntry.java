package com.carpet.rof;

import net.fabricmc.loader.api.FabricLoader;

public class ROFEntry
{
    public static String MOD_ID = "carpet-rof-addition";

    public static String FANCY_NAME = "Carpet ROF Addition";

    public static final String MOD_VER = FabricLoader.getInstance()
        .getModContainer(MOD_ID)
        .orElseThrow(() -> new IllegalStateException("找不到模组：" + MOD_ID))
        .getMetadata()
        .getVersion()
        .getFriendlyString();
}
