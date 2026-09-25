package com.carpet.rof.mixinAccessor;

public interface LevelAccessor
{
    static LevelAccessor of(Object object)
    {
        return (LevelAccessor) object;
    }

    int rof$getBlockChangeStamp();

    void rof$setBlockChangeStamp(int value);

    void rof$blockChangeStampAdd();
}
