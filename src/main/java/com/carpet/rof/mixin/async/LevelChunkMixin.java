package com.carpet.rof.mixin.async;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.LevelChunkAccessor;
import com.carpet.rof.rules.asyncWorldgen.spawner.SpawnResult;
import com.carpet.rof.rules.asyncWorldgen.randomTick.RandomTickResult;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements LevelChunkAccessor
{
    @PublicField
    @Unique
    private volatile RandomTickResult randomTickResult;

    @PublicField
    @Unique
    private volatile SpawnResult spawnResult;

    @Override
    public RandomTickResult rof$getRandomTickResult()
    {
        return this.randomTickResult;
    }

    @Override
    public void rof$setRandomTickResult(RandomTickResult value)
    {
        this.randomTickResult = value;
    }

    @Override
    public SpawnResult rof$getSpawnResult()
    {
        return this.spawnResult;
    }

    @Override
    public void rof$setSpawnResult(SpawnResult value)
    {
        this.spawnResult = value;
    }

}
