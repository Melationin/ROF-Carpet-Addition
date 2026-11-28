package com.carpet.rof.world.explosion;

import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.entity.merge.MergeSetting;
import com.carpet.rof.mixinAccessor.EntityAccessor;
import com.carpet.rof.mixinAccessor.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ExplosionMergeData
{
    // 实体移动、生成与爆炸伤害热路径的快速入口，仅在当前合并组确认不破坏方块期间非空
    public static ExplosionMergeData ACTIVE;

    // 全局递增批次号，确保跨世界、跨合并组的实体暴露度缓存不会串用
    private static int nextStamp;

    private ServerLevel level;
    private double x;
    private double y;
    private double z;
    private float radius;
    private Entity source;
    private AABB entityBox;

    private int count;
    private boolean blockDamageEmpty;
    private boolean forceStopped;
    private int levelStamp;

    private int pendingMergeCount = 1;
    private int mergedCount = 1;

    private int exposureStamp;
    private final List<Entity> entities = new ArrayList<>();

    public static ExplosionMergeData activeFor(ServerExplosion explosion)
    {
        ExplosionMergeData data = ACTIVE;
        return data != null && data.matches(explosion) ? data : null;
    }

    public boolean shouldSkipBlockCalculation(ServerExplosion explosion, ExplosionDamageCalculator calculator)
    {
        OptimizedExplosionStats.onExplosion();
        int requestedMerge = this.pendingMergeCount;
        this.resetMerge();

        if (!OptimizedExplosionUtil.isApplicable(explosion, calculator))
        {
            OptimizedExplosionStats.onUnavailable();
            return false;
        }

        ServerLevel explosionLevel = explosion.level();
        Vec3 center = explosion.center();
        float explosionRadius = explosion.radius();
        if (!this.matches(explosionLevel, center.x, center.y, center.z, explosionRadius))
        {
            this.begin(explosionLevel, center, explosionRadius, explosion.getDirectSourceEntity());
        }
        this.count++;

        if ((this.blockDamageEmpty || this.forceStopped) && this.levelStamp != this.currentBlockStamp())
        {
            this.deactivate();
            this.forceStopped = false;
        }

        if (this.blockDamageEmpty)
        {
            OptimizedExplosionStats.onCachedVerdict();
            return this.skipBlocks(requestedMerge);
        }
        if (this.forceStopped)
        {
            OptimizedExplosionStats.onForceStopped();
            return false;
        }
        if (this.count < OptimizedExplosionSettings.optimizedExplosion)
        {
            OptimizedExplosionStats.onBelowThreshold();
            return false;
        }

        OptimizedExplosionStats.onDryRun();
        if (OptimizedExplosionUtil.worstCaseDestroysBlock(explosion, explosionLevel, calculator, center, explosionRadius))
        {
            this.deactivate();
            this.forceStopped = true;
            this.levelStamp = this.currentBlockStamp();
            OptimizedExplosionStats.onUnsafeVerdict();
            return false;
        }

        this.activate();
        return this.skipBlocks(requestedMerge);
    }

    private boolean skipBlocks(int requestedMerge)
    {
        OptimizedExplosionStats.onSkipped();
        if (MergeSetting.mergeExplosion && requestedMerge > 1)
        {
            this.mergedCount = requestedMerge;
        }
        return true;
    }

    public boolean matches(ServerExplosion explosion)
    {
        Vec3 center = explosion.center();
        return this.matches(explosion.level(), center.x, center.y, center.z, explosion.radius());
    }

    public boolean matches(ServerLevel level, double x, double y, double z, float radius)
    {
        return this.level == level && this.x == x && this.y == y && this.z == z && this.radius == radius;
    }

    private void begin(ServerLevel level, Vec3 center, float radius, Entity source)
    {
        this.releaseGroup();
        this.level = level;
        this.x = center.x;
        this.y = center.y;
        this.z = center.z;
        this.radius = radius;
        this.source = source;
        this.entityBox = OptimizedExplosionUtil.makeEntityBox(center, radius);
    }

    private void activate()
    {
        this.blockDamageEmpty = true;
        this.forceStopped = false;
        int stamp = ++nextStamp;
        this.exposureStamp = stamp <= 0 ? (nextStamp = 1) : stamp;
        this.entities.clear();
        this.entities.addAll(this.level.getEntities(this.source, this.entityBox));
        this.levelStamp = this.currentBlockStamp();
        ACTIVE = this;
    }

    private void deactivate()
    {
        if (ACTIVE == this) ACTIVE = null;
        this.blockDamageEmpty = false;
    }

    public void requestMerge(int count)
    {
        this.pendingMergeCount = Math.max(1, count);
        this.mergedCount = 1;
    }

    public void resetMerge()
    {
        this.pendingMergeCount = 1;
        this.mergedCount = 1;
    }

    public int mergedCount()
    {
        return this.mergedCount;
    }

    public void clear()
    {
        if (this.level != null) this.releaseGroup();
        this.resetMerge();
    }

    private void releaseGroup()
    {
        this.deactivate();
        this.level = null;
        this.source = null;
        this.entityBox = null;
        this.count = 0;
        this.forceStopped = false;
        this.exposureStamp = 0;
        this.levelStamp = 0;
        this.entities.clear();
    }

    private int currentBlockStamp()
    {
        return LevelAccessor.of(this.level).rof$getBlockChangeStamp();
    }

    public void tryAddEntity(Entity entity)
    {
        this.tryAddEntity(entity, entity.getX(), entity.getY(), entity.getZ());
    }

    public void tryAddEntity(Entity entity, double x, double y, double z)
    {
        if (!this.blockDamageEmpty || entity == this.source || entity.isRemoved() || entity.level() != this.level) return;
        double halfWidth = entity.getBbWidth() / 2.0;
        double height = entity.getBbHeight();
        AABB box = new AABB(x - halfWidth, y, z - halfWidth, x + halfWidth, y + height, z + halfWidth);
        if (box.intersects(this.entityBox) && !this.entities.contains(entity))
        {
            this.entities.add(entity);
        }
    }

    public List<Entity> entitySnapshot()
    {
        List<Entity> snapshot = new ArrayList<>(this.entities.size());
        for (int i = 0; i < this.entities.size(); i++)
        {
            Entity entity = this.entities.get(i);
            if (!entity.isRemoved()) snapshot.add(entity);
        }
        return snapshot;
    }

    /*
     * 实体在当前合并组内算过暴露度且未发生过位移（setPosRaw 会将 stamp 置为 -1）时直接复用。
     */
    public float cachedExposure(Entity entity)
    {
        EntityAccessor access = EntityAccessor.of(entity);
        int stamp = access.rof$getExposureStamp();
        if (stamp != this.exposureStamp)
        {
            OptimizedExplosionStats.onExposureMiss(stamp);
            return Float.NaN;
        }
        OptimizedExplosionStats.onExposureReused();
        return access.rof$getCachedExposure();
    }

    public void cacheExposure(Entity entity, float value)
    {
        EntityAccessor access = EntityAccessor.of(entity);
        access.rof$setCachedExposure(value);
        access.rof$setExposureStamp(this.exposureStamp);
    }
}
