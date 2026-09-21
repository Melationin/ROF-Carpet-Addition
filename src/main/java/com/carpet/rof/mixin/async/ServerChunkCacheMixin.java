package com.carpet.rof.mixin.async;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.asyncWorldgen.DelayedChunkSnapshot;
import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import com.carpet.rof.rules.asyncWorldgen.spawner.AsyncNaturalSpawner;
import com.carpet.rof.rules.asyncWorldgen.randomTick.AsyncRandomTick;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @WrapOperation(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/server/level/ChunkMap;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V"))
    private void rof$cacheBlockTickingChunks(ChunkMap map, Consumer<LevelChunk> consumer, Operation<Void> original)
    {
        DelayedChunkSnapshot snapshot = ExtraWorldDatas.fromWorld(level).randomTickingChunks;
        if (!AsyncSettings.randomTickChunkCache)
        {
            snapshot.clear();
            original.call(map, consumer);

        }else {
            long tick = level.getServer().getTickCount();
            if (!snapshot.isFresh(tick))
                original.call(map, (Consumer<LevelChunk>) snapshot.beginRefresh(tick)::add);
            snapshot.chunks().forEach(consumer);
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
