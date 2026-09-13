package com.carpet.rof.rules.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * 同一世界、同一点、同一威力的连续爆炸的合并元数据。
 * 只在当前游戏刻内有效：世界 tick 开始、或世界中有任何方块变化时都会被清空。
 */
public class ExplosionMergeData
{
    /** 实体移动与实体生成的热路径快速查找入口，仅在启用优化期间非空 */
    public static ExplosionMergeData ACTIVE;

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
    public final List<Entity> entities = new ArrayList<>();

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
        this.entities.clear();
        this.entities.addAll(this.level.getEntities(this.source, this.entityBox));
        ACTIVE = this;
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
        this.entities.clear();
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
        if (box.intersects(this.entityBox)) this.entities.add(entity);
    }

    public List<Entity> entitySnapshot()
    {
        this.entities.removeIf(Entity::isRemoved);
        return new ArrayList<>(this.entities);
    }
}
