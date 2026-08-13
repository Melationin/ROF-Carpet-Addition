package com.carpet.rof.utils.asyncWorldgen.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;


public record SpawnCandidate(MobCategory category,
                             int groupIndex,
                             BlockPos pos,
                             MobSpawnSettings.SpawnerData spawnData,
                             boolean staticChecksPassed,
                             boolean placementChecksPassed)
{
}
