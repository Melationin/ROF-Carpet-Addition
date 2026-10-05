package com.carpet.rof.mixinAccessor;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;

public interface SpawnStateSimplifyAccess
{
    void rof$simplifySpawnStatistic(Object2IntOpenHashMap<MobCategory> categoryCounts);
}
