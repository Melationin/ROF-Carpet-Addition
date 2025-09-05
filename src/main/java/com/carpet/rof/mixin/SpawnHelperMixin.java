package com.carpet.rof.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.ROFCarpetSettings.optimizeSpawnAttempts;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {

    @Inject(method = "isAcceptableSpawnPosition",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkPos;<init>(Lnet/minecraft/util/math/BlockPos;)V"),cancellable = true)
    private static void isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        if(!optimizeSpawnAttempts) return;
        BlockState state = world.getBlockState(pos.add(0,-1,0));
        if( (!state.isSolidBlock(world, pos.add(0,-1,0)))&& state.getFluidState().getFluid() == Fluids.EMPTY) {
            cir.setReturnValue(false);
        }
    }
}
