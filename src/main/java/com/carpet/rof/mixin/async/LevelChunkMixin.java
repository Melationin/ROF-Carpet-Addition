package com.carpet.rof.mixin.async;

import com.carpet.rof.utils.asyncWorldgen.spawner.AsyncNaturalSpawnerChunk;
import com.carpet.rof.utils.asyncWorldgen.randomTick.AsyncRandomTickChunk;
import com.carpet.rof.utils.asyncWorldgen.spawner.SpawnResult;
import com.carpet.rof.utils.asyncWorldgen.randomTick.RandomTickResult;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements AsyncRandomTickChunk, AsyncNaturalSpawnerChunk
{
    @Unique
    private volatile RandomTickResult rof$randomTickResult;
    @Unique
    private volatile SpawnResult rof$spawnResult;

    public RandomTickResult rof$getAsyncRandomTickResult()
    {
        return rof$randomTickResult;
    }

    public void rof$setAsyncRandomTickResult(RandomTickResult result)
    {
        rof$randomTickResult = result;
    }

    public SpawnResult rof$getAsyncSpawnResult()
    {
        return rof$spawnResult;
    }

    public void rof$setAsyncSpawnResult(SpawnResult result)
    {
        rof$spawnResult = result;
    }
}
