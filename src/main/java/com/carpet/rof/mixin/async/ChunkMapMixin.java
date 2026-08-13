package com.carpet.rof.mixin.async;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.asyncWorldgen.AsyncWorldgenCacheData;
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
        AsyncWorldgenCacheData cache = ExtraWorldDatas.fromWorld(level).asyncWorldgenCache;
        boolean enabled = AsyncSettings.spawningChunkCache;
        if (cache.spawningCacheWasEnabled != enabled) {
            cache.spawningCacheWasEnabled = enabled;
            cache.invalidateSpawning();
        }
        if (!enabled)
            return;
        long tick = level.getServer().getTickCount();
        if (cache.spawningChunksTick >= 0 && tick - cache.spawningChunksTick < AsyncWorldgenCacheData.REFRESH_INTERVAL) {
            out.addAll(cache.spawningChunks);
            ci.cancel();
        }
    }

    @Inject(method = "collectSpawningChunks",
            at = @At("TAIL"))
    private void rof$cacheSpawningChunks(List<LevelChunk> result, CallbackInfo ci)
    {
        AsyncWorldgenCacheData cache = ExtraWorldDatas.fromWorld(level).asyncWorldgenCache;
        if (!AsyncSettings.spawningChunkCache)
            return;
        cache.spawningChunks.clear();
        cache.spawningChunks.addAll(result);
        cache.spawningChunksTick = level.getServer().getTickCount();
        cache.spawningCacheWasEnabled = true;
    }
}
