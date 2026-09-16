package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.rules.spawnStatistic.LocalMobCapLevelAccess;
import com.carpet.rof.rules.spawnStatistic.SpawnStatisticSimplifyAccess;
import com.carpet.rof.rules.spawnStatistic.SpawnStatisticSimplifyUtil;
import com.carpet.rof.rules.spawnStatistic.SpawnStateSimplifyAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin
{
    @WrapOperation(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;createState(ILjava/lang/Iterable;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;"))
    private static NaturalSpawner.SpawnState rof$simplifySpawnStatistic(int spawnableChunkCount, Iterable<Entity> entities, NaturalSpawner.ChunkGetter chunkGetter, LocalMobCapCalculator localMobCapCalculator, Operation<NaturalSpawner.SpawnState> original)
    {
        ServerLevel level = ((LocalMobCapLevelAccess)localMobCapCalculator).rof$localMobCapLevel();
        if (!((SpawnStatisticSimplifyAccess)level).rof$isSpawnStatisticSimplified()) {
            return original.call(spawnableChunkCount, entities, chunkGetter, localMobCapCalculator);
        }
        // 用一个空列表让原版只负责造出 SpawnState，计数与局部上限统计由化简路径自己填。
        NaturalSpawner.SpawnState state = original.call(spawnableChunkCount, List.of(), chunkGetter, localMobCapCalculator);
        ((SpawnStateSimplifyAccess)state).rof$simplifySpawnStatistic(SpawnStatisticSimplifyUtil.countMobs(entities, localMobCapCalculator));
        return state;
    }
}
