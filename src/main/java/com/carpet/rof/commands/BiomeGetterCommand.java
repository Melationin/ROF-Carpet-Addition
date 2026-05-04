package com.carpet.rof.commands;

import com.carpet.rof.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biomes;


//@ROFCommand
public class BiomeGetterCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {

        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("getBiome").command(context -> {
            if(context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
                int sum = 0;
                int rsum = 0;

                var pos = serverPlayer.blockPosition();

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
                        if(context.getSource().getLevel().getBiome(new BlockPos(x,pos.getY(),z)).is(Biomes.RIVER)){
                            rsum++;
                        }
                    }
                }
                int finalRsum = rsum;
                int finalSum = sum;
                context.getSource().sendSuccess(()-> Component.nullToEmpty(finalRsum*1.0/finalSum +""),false);
            }
            return 1;
        });
    }
}
