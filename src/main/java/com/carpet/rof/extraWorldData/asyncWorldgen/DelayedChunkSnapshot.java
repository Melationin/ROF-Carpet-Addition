package com.carpet.rof.extraWorldData.asyncWorldgen;

import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public final class DelayedChunkSnapshot
{
    public static final int REFRESH_INTERVAL = 40;

    private final List<LevelChunk> chunks = new ArrayList<>();
    private long refreshedAtTick = -1;

    public boolean isFresh(long tick)
    {
        return refreshedAtTick >= 0 && tick - refreshedAtTick < REFRESH_INTERVAL;
    }

    public List<LevelChunk> beginRefresh(long tick)
    {
        chunks.clear();
        refreshedAtTick = tick;
        return chunks;
    }

    public List<LevelChunk> chunks()
    {
        return chunks;
    }

    public void clear()
    {
        chunks.clear();
        refreshedAtTick = -1;
    }
}
