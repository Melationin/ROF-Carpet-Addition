package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.rules.explosion.ExplosionMergeData;
import com.carpet.rof.rules.explosion.OptimizedExplosionUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ServerExplosion.class,priority = 500)
public abstract class ServerExplosionMixin
{
    @Shadow
    @Final
    private ExplosionDamageCalculator damageCalculator;


    @WrapOperation(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;calculateExplodedPositions()Ljava/util/List;"))
    private List<BlockPos> rof$optimizeExplosionBlocks(ServerExplosion instance, Operation<List<BlockPos>> original)
    {
        OptimizedExplosionStats.onExplosion();
        if (OptimizedExplosionUtil.shouldSkipBlockCalculation(instance, this.damageCalculator))
        {
            OptimizedExplosionStats.onSkipped();
            return new ObjectArrayList<>();
        }
        return original.call(instance);
    }

    @WrapOperation(
            method = "hurtEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> rof$useTrackedEntities(ServerLevel level, Entity source, AABB box, Operation<List<Entity>> original)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null && data.enabled)
        {
            ServerExplosion self = (ServerExplosion) (Object) this;
            Vec3 center = self.center();
            if (data.matches(level, center.x, center.y, center.z, self.radius()))
            {
                return data.entitySnapshot();
            }
        }
        return original.call(level, source, box);
    }
}
