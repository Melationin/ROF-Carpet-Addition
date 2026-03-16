package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;


//@ROFCommand
public class BiomeGetterCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("getBiome").command(context -> {
            if(context.getSource().getPlayer() instanceof ServerPlayerEntity serverPlayer){
                int sum = 0;
                int rsum = 0;

                var pos = serverPlayer.getBlockPos();

                int x1 = pos.getX() - 128;
                int z1 = pos.getZ() - 128;
                int x2 = pos.getX() + 128;
                int z2 = pos.getZ() + 128;

                for(int x = x1; x <= x2; ++x){
                    for(int z = z1; z <= z2; ++z){
                        int dx = x - pos.getX();
                        int dz = z - pos.getZ();
                        int d = dx * dx + dz * dz;
                        if(d>=128*128 || d < 24*24) continue;
                        sum++;
                        if(context.getSource().getWorld().getBiome(new BlockPos(x,pos.getY(),z)).matchesKey(BiomeKeys.RIVER)){
                            rsum++;
                        }
                    }
                }
                int finalRsum = rsum;
                int finalSum = sum;
                context.getSource().sendFeedback(()->Text.of(finalRsum*1.0/finalSum +""),false);
            }
            return 1;
        });
    }
}
