package com.carpet.rof.rules.spawnStatistic;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;

public final class SpawnStatisticSimplifyUtil
{
    private SpawnStatisticSimplifyUtil()
    {
    }

    // 原版这里是每实体一次区块查找加一次生物群系查询，化简时只保留分类计数与局部上限统计。
    public static Object2IntOpenHashMap<MobCategory> countMobs(Iterable<Entity> entities, LocalMobCapCalculator localMobCapCalculator)
    {
        Object2IntOpenHashMap<MobCategory> mobCounts = new Object2IntOpenHashMap<>();
        for (Entity entity : entities) {
            if (entity instanceof Mob mob && (mob.isPersistenceRequired() || mob.requiresCustomPersistence())) {
                continue;
            }
            MobCategory category = entity.getType().getCategory();
            if (category == MobCategory.MISC) {
                continue;
            }
            BlockPos pos = entity.blockPosition();
            if (entity instanceof Mob) {
                localMobCapCalculator.addMob(ChunkPos.containing(pos), category);
            }
            mobCounts.addTo(category, 1);
        }
        return mobCounts;
    }
}
