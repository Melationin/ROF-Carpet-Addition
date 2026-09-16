package com.carpet.rof.rules.spawnStatistic;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;

public interface SpawnStateSimplifyAccess
{
    void rof$simplifySpawnStatistic(Object2IntOpenHashMap<MobCategory> categoryCounts);
}
