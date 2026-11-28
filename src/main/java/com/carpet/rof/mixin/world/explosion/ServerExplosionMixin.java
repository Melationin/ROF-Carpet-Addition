package com.carpet.rof.mixin.world.explosion;

import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.world.explosion.ExplosionMergeData;
import com.carpet.rof.world.explosion.OptimizedExplosionUtil;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ServerExplosion.class, priority = 500)
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
        if (OptimizedExplosionUtil.shouldSkipBlockCalculation(instance, this.damageCalculator))
        {
            return new ObjectArrayList<>();
        }
        return original.call(instance);
    }

    @WrapOperation(
            method = "hurtEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> rof$useTrackedEntities(ServerLevel level, Entity source, AABB box, Operation<List<Entity>> original)
    {
        ExplosionMergeData data = this.rof$activeGroup();
        List<Entity> entities = data != null ? data.entitySnapshot() : original.call(level, source, box);
        OptimizedExplosionStats.onEntityQuery(entities.size());
        return entities;
    }

    // 被合并掉的爆炸不会再 push，这里按合并次数补齐推力，等价于原版多次 push 的叠加
    @WrapOperation(
            method = "hurtEntities",
            //? <26.3
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/phys/Vec3;)V"
            //?>= 26.3
            //at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pushFromExplosion(Lnet/minecraft/world/phys/Vec3;)V"
            ))
    private void rof$mergeKnockback(Entity instance, Vec3 impulse, Operation<Void> original)
    {

        ExplosionMergeData data = this.rof$activeGroup();
        if(data == null){
            original.call(instance, impulse);
        }else {
            for(int i = 0;i < data.mergedCount();i++){
                original.call(instance, impulse);
            }
        }
    }

    /*
     * 同一合并组内、实体没有移动时，暴露度（getSeenPercent）可以复用：
     * 它只由实体碰撞箱、坐标、所在世界、沿途方块与本组数据决定。
     */
    @WrapOperation(
            method = "hurtEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;getSeenPercent(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)F"))
    private float rof$reuseExposure(Vec3 center, Entity entity, Operation<Float> original)
    {
        ExplosionMergeData data = this.rof$activeGroup();
        if (data == null)
        {
            OptimizedExplosionStats.onExposureUnmanaged();
            return original.call(center, entity);
        }
        float cached = data.cachedExposure(entity);
        if (!Float.isNaN(cached)) return cached;
        float computed = original.call(center, entity);
        OptimizedExplosionStats.onExposureComputed();
        data.cacheExposure(entity, computed);
        return computed;
    }

    @Unique
    private ExplosionMergeData rof$activeGroup()
    {
        return ExplosionMergeData.activeFor((ServerExplosion) (Object) this);
    }
}
