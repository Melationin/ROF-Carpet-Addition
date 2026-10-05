package com.carpet.rof.mixin.world.chunkTick.async;

import com.carpet.rof.mixinAccessor.EntityTypeAccessor;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnPlacements.class)
public class SpawnPlacementsMixin
{
    @Inject(method = "register", at = @At("HEAD"))
    private static void rof$resetAsyncSpawnRule(EntityType<?> type, SpawnPlacementType placement,
                                              Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<?> predicate, CallbackInfo ci)
    {
        EntityTypeAccessor.of(type).rof$setAsyncSpawnRule(false);
    }

    @Definition(id = "register", method = "Lnet/minecraft/world/entity/SpawnPlacements;register(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/SpawnPlacementType;Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V")
    @Definition(id = "checkMonsterSpawnRules", method = "Lnet/minecraft/world/entity/monster/Monster;checkMonsterSpawnRules(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z")
    @Expression("register(?, ?, ?, ::checkMonsterSpawnRules)")
    @WrapOperation(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void rof$registerAsyncSpawnRule(EntityType<?> type, SpawnPlacementType placement,
                                                  Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<?> predicate, Operation<Void> original)
    {
        original.call(type, placement, heightmap, predicate);
        EntityTypeAccessor.of(type).rof$setAsyncSpawnRule(true);
    }
}
