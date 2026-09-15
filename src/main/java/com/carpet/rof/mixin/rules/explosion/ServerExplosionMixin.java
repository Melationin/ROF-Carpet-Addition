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
        List<Entity> entities;
        if (data != null && data.enabled && isSameGroup(data))
        {
            entities = data.entitySnapshot();
        }
        else
        {
            entities = original.call(level, source, box);
        }
        OptimizedExplosionStats.onEntityQuery(entities.size());
        return entities;
    }

    /**
     * 同一合并组内、实体没有移动时，暴露度（getSeenPercent）可以复用：
     * 它只由实体碰撞箱、坐标、所在世界、沿途方块与本组数据决定。
     * 这是 hurtEntities 里对 getSeenPercent 的唯一调用点（ServerExplosion.java:189）。
     * 伤害与击退仍然逐次结算，不做任何合并。
     */
    @WrapOperation(
            method = "hurtEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;getSeenPercent(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)F"))
    private float rof$reuseExposure(Vec3 center, Entity entity, Operation<Float> original)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null && data.enabled && isSameGroup(data))
        {
            float cached = data.cachedExposure(entity);
            if (!Float.isNaN(cached)) return cached;
            float computed = original.call(center, entity);
            OptimizedExplosionStats.onExposureComputed();
            data.cacheExposure(entity, computed);
            return computed;
        }
        OptimizedExplosionStats.onExposureUnmanaged();
        return original.call(center, entity);
    }

    private boolean isSameGroup(ExplosionMergeData data)
    {
        ServerExplosion self = (ServerExplosion) (Object) this;
        Vec3 center = self.center();
        return data.matches(self.level(), center.x, center.y, center.z, self.radius());
    }
}
