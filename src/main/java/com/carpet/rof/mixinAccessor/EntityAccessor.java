package com.carpet.rof.mixinAccessor;

public interface EntityAccessor
{
    static EntityAccessor of(Object object)
    {
        return (EntityAccessor) object;
    }

    long rof$getTickOrder();

    void rof$setTickOrder(long value);

    int rof$getExposureStamp();

    void rof$setExposureStamp(int value);

    int rof$getExposureIndex();

    void rof$setExposureIndex(int value);
}
