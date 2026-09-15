package com.carpet.rof.rules.explosion;

import com.carpet.rof.blockChange.LevelBlockChangeAccess;
import com.carpet.rof.debug.OptimizedExplosionStats;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * 同一世界、同一点、同一威力的连续爆炸的合并元数据。
 * 只在当前游戏刻内有效（世界 tick 开始时清空）；方块判定在世界方块戳记未变化前可以复用。
 */
public class ExplosionMergeData
{
    // 实体移动与实体生成的热路径快速查找入口，仅在启用优化期间非空
    public static ExplosionMergeData ACTIVE;

    // 批次号发号器：全局递增，保证不同世界、不同合并组的批次号不会撞车
    private static int nextStamp;

    public ServerLevel level;
    public double x;
    public double y;
    public double z;
    public float radius;
    public int count;
    public AABB entityBox;
    public Entity source;
    public boolean forceStopped;
    public boolean blockDamageEmpty;
    public boolean enabled;
    public int needExplosionCount = 1;
    public int explosionCount = 1;
    // 本组的批次号，实体身上记的就是它；0 表示本组还没开始缓存
    public int exposureStamp;
    public final List<Entity> entities = new ArrayList<>();
    // 与 entities 下标一一对应的暴露度缓存，NaN 表示这个实体在本组里还没算过
    public final DoubleArrayList exposures = new DoubleArrayList();

    // 做出方块判定时世界的方块变更戳记；戳记一变，forceStopped 和 blockDamageEmpty 都必须重算
    public int levelStamp;

    public boolean isUsed()
    {
        return this.level != null;
    }

    public boolean matches(ServerLevel level, double x, double y, double z, float radius)
    {
        return this.level == level && this.x == x && this.y == y && this.z == z && this.radius == radius;
    }

    public void begin(ServerLevel level, double x, double y, double z, float radius, Entity source, AABB entityBox)
    {
        this.release();
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.source = source;
        this.entityBox = entityBox;
    }

    public void markBlockDamageEmpty()
    {
        this.blockDamageEmpty = true;
        this.enabled = true;
        int stamp = ++nextStamp;
        this.exposureStamp = stamp == 0 ? ++nextStamp : stamp;
        this.entities.clear();
        this.entities.addAll(this.level.getEntities(this.source, this.entityBox));
        this.resetExposureCache();
        this.captureBlockStamp();
        ACTIVE = this;
    }

    public void captureBlockStamp()
    {
        this.levelStamp = ((LevelBlockChangeAccess) this.level).rof$getBlockChangeStamp();
    }

    public boolean isBlockVerdictValid()
    {
        return this.levelStamp == ((LevelBlockChangeAccess) this.level).rof$getBlockChangeStamp();
    }

    public void clear()
    {
        if (this.isUsed()) this.release();
    }

    private void release()
    {
        if (ACTIVE == this) ACTIVE = null;
        this.level = null;
        this.source = null;
        this.entityBox = null;
        this.count = 0;
        this.forceStopped = false;
        this.blockDamageEmpty = false;
        this.enabled = false;
        this.needExplosionCount = 1;
        this.explosionCount = 1;
        this.exposureStamp = 0;
        this.levelStamp = 0;
        this.entities.clear();
        this.exposures.clear();
    }

    public void tryAddEntity(Entity entity)
    {
        if (!this.enabled || entity == this.source || entity.isRemoved() || entity.level() != this.level) return;
        this.tryAddEntity(entity, entity.getX(), entity.getY(), entity.getZ());
    }

    public void tryAddEntity(Entity entity, double x, double y, double z)
    {
        if (!this.enabled || entity == this.source || entity.isRemoved() || entity.level() != this.level) return;
        if (this.entities.contains(entity)) return;
        double halfWidth = entity.getBbWidth() / 2.0;
        double height = entity.getBbHeight();
        AABB box = new AABB(x - halfWidth, y, z - halfWidth, x + halfWidth, y + height, z + halfWidth);
        if (box.intersects(this.entityBox))
        {
            this.entities.add(entity);
            this.exposures.add(Double.NaN);
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

    /**
     * 复用暴露度的条件只有一个：这个实体在本组里算过、而且算完之后没有动过。
     * 实体自己记着算缓存时的批次号（ExposureCacheAccess），而 setPosRaw / setBoundingBox
     * 会把有效批次号改成 -1，所以这里只要比一个 int；位置或碰撞箱一变就自动重算，不用比较 AABB。
     */
    public float cachedExposure(Entity entity)
    {
        ExposureCacheAccess access = (ExposureCacheAccess) entity;
        int stamp = access.rof$getExposureStamp();
        if (stamp != this.exposureStamp)
        {
            OptimizedExplosionStats.onExposureMiss(stamp);
            return Float.NaN;
        }
        int index = access.rof$getExposureIndex();
        if (index < 0 || index >= this.exposures.size())
        {
            OptimizedExplosionStats.onExposureMissIndex();
            return Float.NaN;
        }
        OptimizedExplosionStats.onExposureReused();
        return (float) this.exposures.getDouble(index);
    }

    public void cacheExposure(Entity entity, float value)
    {
        int index = this.entities.indexOf(entity);
        if (index < 0) return;
        this.exposures.set(index, value);
        ExposureCacheAccess access = (ExposureCacheAccess) entity;
        access.rof$setExposureIndex(index);
        access.rof$setExposureStamp(this.exposureStamp);
    }

    private void resetExposureCache()
    {
        this.exposures.clear();
        for (int i = 0; i < this.entities.size(); i++)
        {
            this.exposures.add(Double.NaN);
        }
    }
}
