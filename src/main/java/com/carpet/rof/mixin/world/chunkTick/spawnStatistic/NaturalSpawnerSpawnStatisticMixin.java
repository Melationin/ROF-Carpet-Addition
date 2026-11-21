package com.carpet.rof.mixin.world.chunkTick.spawnStatistic;

import com.carpet.rof.mixinAccessor.ServerLevelAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerSpawnStatisticMixin
{
    // 26.3 起 createState 自己从 level 取实体
    //? if>=26.3 {
    /*@WrapOperation(method = "createState(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/server/level/ServerLevel;getAllEntities()Ljava/lang/Iterable;"
                   ))
    private static Iterable<Entity> rof$skipSpawnStatisticEntities(ServerLevel level, Operation<Iterable<Entity>> original)
    {
        if (ServerLevelAccessor.of(level).rof$getSpawnStatisticSimplified()) {
            return List.of();
        }
        return original.call(level);
    }
    *///?}
}
