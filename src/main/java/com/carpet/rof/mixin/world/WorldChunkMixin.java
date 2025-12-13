package com.carpet.rof.mixin.world;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Shadow public abstract Map<BlockPos, BlockEntity> getBlockEntities();

    //@Shadow @Final private Map<BlockPos, WorldChunk.WrappedBlockEntityTickInvoker> blockEntityTickers;

    @Inject(method = "updateAllBlockEntities",at = @At(value = "HEAD"),cancellable = true)
    private void updateAllBlockEntities(CallbackInfo ci) {

    }
}
