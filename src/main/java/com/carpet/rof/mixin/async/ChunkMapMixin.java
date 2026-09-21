package com.carpet.rof.mixin.async;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.asyncWorldgen.DelayedChunkSnapshot;
import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkMap.class)
public class ChunkMapMixin
{
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "collectSpawningChunks",
            at = @At("HEAD"),
            cancellable = true)
    private void rof$useCachedSpawningChunks(List<LevelChunk> out, CallbackInfo ci)
    {
        DelayedChunkSnapshot snapshot = ExtraWorldDatas.fromWorld(level).spawningChunks;
        if (!AsyncSettings.spawningChunkCache)
        {
            snapshot.clear();
            return;
        }
        if (snapshot.isFresh(level.getServer().getTickCount()))
        {
            out.addAll(snapshot.chunks());
            ci.cancel();
        }
    }

    @Inject(method = "collectSpawningChunks",
            at = @At("TAIL"))
    private void rof$cacheSpawningChunks(List<LevelChunk> result, CallbackInfo ci)
    {
        if (!AsyncSettings.spawningChunkCache)
            return;
        ExtraWorldDatas.fromWorld(level).spawningChunks
                .beginRefresh(level.getServer().getTickCount()).addAll(result);
    }
}
