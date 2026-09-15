package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.explosion.OptimizedExplosionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin
{

    @Inject(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("HEAD"))
    private void rof$invalidateExplosionMergeData(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir)
    {
        if (!OptimizedExplosionSettings.enabled()) return;
        Level level = ((LevelChunk) (Object) this).getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;
        ExtraWorldDatas.fromWorld(serverLevel).explosionMergeData.clear();
    }
}
