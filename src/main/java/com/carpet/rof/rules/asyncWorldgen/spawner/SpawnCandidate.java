package com.carpet.rof.rules.asyncWorldgen.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;


public record SpawnCandidate(MobCategory category,
                             int groupIndex,
                             BlockPos pos,
                             MobSpawnSettings.SpawnerData spawnData,
                             boolean staticChecksPassed,
                             boolean placementChecksPassed,
                             Collision collision)
{
    record Collision(AABB box, LevelChunk[] chunks, long blockChangeStampSum)
    {
    }
}
