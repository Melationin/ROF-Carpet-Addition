package com.carpet.rof.rules.getBiomeLayerCache;

import net.minecraft.world.chunk.Chunk;

public interface ServerWorldAccessor {
    Chunk getNowChunk();

    void setNowChunk(Chunk nowChunk);


}
