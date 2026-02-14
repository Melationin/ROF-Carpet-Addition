package com.carpet.rof.utils;

import net.minecraft.util.math.BlockPos;

public class ROFTextTool
{
    public static String getWorldName(String worldName){
        if(worldName.contains("overworld")){
            return "&r&2&l"+"OVERWORLD";
        }else if(worldName.contains("the_nether")){
            return "&r&4&l"+"THE NETHER";
        }else if(worldName.contains("the_end")){
            return "&r&5&l"+"THE END";
        }
        return "&r&l"+worldName.toUpperCase();
    }

    public static String getStringToClip(BlockPos pos){
        return String.format("%d %d %d",pos.getX(),pos.getY(),pos.getZ());
    }
}
