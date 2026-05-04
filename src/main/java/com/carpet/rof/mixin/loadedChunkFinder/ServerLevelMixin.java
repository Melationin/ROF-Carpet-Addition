package com.carpet.rof.mixin.loadedChunkFinder;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Inject(method = "tickChunk",
            at = @At(value = "HEAD"))
    public void tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        var manager = ExtraWorldDatas.fromWorld((ServerLevel) (Object)this).chunkLoadedFinder;
        if(manager.needLog){
            manager.ChunkLoadedMap.add(chunk.getPos());
        }
    }
}
