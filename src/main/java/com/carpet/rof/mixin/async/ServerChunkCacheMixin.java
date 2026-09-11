package com.carpet.rof.mixin.async;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.asyncWorldgen.AsyncWorldgenCacheData;
import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import com.carpet.rof.rules.asyncWorldgen.spawner.AsyncNaturalSpawner;
import com.carpet.rof.rules.asyncWorldgen.randomTick.AsyncRandomTick;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin
{
    @Shadow
    @Final
    public ChunkMap chunkMap;
    @Shadow
    @Final
    private ServerLevel level;

    @Redirect(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/server/level/ChunkMap;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V"))
    private void rof$cacheBlockTickingChunks(ChunkMap map, Consumer<LevelChunk> consumer)
    {
        AsyncWorldgenCacheData cache = ExtraWorldDatas.fromWorld(level).asyncWorldgenCache;
        boolean enabled = AsyncSettings.randomTickChunkCache;
        if (cache.randomTickingCacheWasEnabled != enabled) {
            cache.randomTickingCacheWasEnabled = enabled;
            cache.invalidateRandomTicking();
        }
        long tick = level.getServer().getTickCount();
        if (!enabled) {
            map.forEachBlockTickingChunk(consumer);
        } else {
            if (cache.randomTickingChunksTick < 0 || tick - cache.randomTickingChunksTick >= AsyncWorldgenCacheData.REFRESH_INTERVAL) {
                cache.randomTickingChunks.clear();
                map.forEachBlockTickingChunk(cache.randomTickingChunks::add);
                cache.randomTickingChunksTick = tick;
            }
            cache.randomTickingChunks.forEach(consumer);
        }
        AsyncRandomTick.submitBatch(level.getServer());
    }

    @Inject(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(value = "CONSTANT",
                     args = "stringValue=tickTickingChunks",
                     shift = At.Shift.BEFORE))
    private void rof$submitSpawningBatch(ProfilerFiller profiler, long timeDiff, CallbackInfo ci)
    {
        AsyncNaturalSpawner.submitBatch(level.getServer());
    }
}
