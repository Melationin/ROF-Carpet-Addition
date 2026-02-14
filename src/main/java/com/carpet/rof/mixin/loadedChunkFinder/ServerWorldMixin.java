package com.carpet.rof.mixin.loadedChunkFinder;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{
    @Inject(method = "tickChunk",
            at = @At(value = "HEAD"))
    public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        var manager = ExtraWorldDatas.fromWorld((ServerWorld) (Object)this).loadedChunkManager;
        if(manager.needLog){
            manager.loadedChunks.add(chunk.getPos());
        }
    }
}
