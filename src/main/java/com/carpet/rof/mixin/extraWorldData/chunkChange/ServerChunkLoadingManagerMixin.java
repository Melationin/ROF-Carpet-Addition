package com.carpet.rof.mixin.extraWorldData.chunkChange;

import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkModifyData;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ServerChunkLoadingManager.class)
public abstract class  ServerChunkLoadingManagerMixin {
    @Final
    @Shadow
    ServerWorld world;
    @Inject(
            method = "getProtoChunk",
            at = @At("HEAD")
    )
    private void rof$onMakeChunkAccessible(ChunkPos pos, CallbackInfoReturnable<Chunk> cir
    ) {
        ChunkModifyData.get(world).createChunk(pos.toLong(),world.getTime());
    }
}
