package com.carpet.rof.mixin.rules.enderPearl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin
{
    @Inject(method = "addEnderPearlTicket",at = @At(value = "TAIL"))
    private static void s(ServerWorld world, ChunkPos chunkPos, CallbackInfoReturnable<Long> cir){
        world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
    }
}
