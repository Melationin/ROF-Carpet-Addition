package com.carpet.rof.mixinAccessor;

public interface LevelChunkAccessor
{
    static LevelChunkAccessor of(Object object)
    {
        return (LevelChunkAccessor) object;
    }

    int rof$getRof$blockChangeStamp();
}
