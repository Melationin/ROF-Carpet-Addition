package com.carpet.rof.mixinAccessor;

import com.carpet.rof.rules.asyncWorldgen.randomTick.RandomTickResult;
import com.carpet.rof.rules.asyncWorldgen.spawner.SpawnResult;

public interface LevelChunkAccessor
{
    static LevelChunkAccessor of(Object object)
    {
        return (LevelChunkAccessor) object;
    }

    RandomTickResult rof$getRandomTickResult();

    void rof$setRandomTickResult(RandomTickResult value);

    SpawnResult rof$getSpawnResult();

    void rof$setSpawnResult(SpawnResult value);

    int rof$getBlockChangeStamp();
}
