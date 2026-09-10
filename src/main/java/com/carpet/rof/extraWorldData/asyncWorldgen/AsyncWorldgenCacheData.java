package com.carpet.rof.extraWorldData.asyncWorldgen;

import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public final class AsyncWorldgenCacheData {
    public static final int REFRESH_INTERVAL = 40;

    public final List<LevelChunk> randomTickingChunks = new ArrayList<>();
    public long randomTickingChunksTick = -1;
    public boolean randomTickingCacheWasEnabled;

    public final List<LevelChunk> spawningChunks = new ArrayList<>();
    public long spawningChunksTick = -1;
    public boolean spawningCacheWasEnabled;

    public void invalidateRandomTicking() {
        randomTickingChunks.clear();
        randomTickingChunksTick = -1;
    }

    public void invalidateSpawning() {
        spawningChunks.clear();
        spawningChunksTick = -1;
    }
}
