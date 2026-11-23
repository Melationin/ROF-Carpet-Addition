package com.carpet.rof.mixinAccessor;

public interface EntityAccessor
{
    static EntityAccessor of(Object object)
    {
        return (EntityAccessor) object;
    }

    int rof$getExposureStamp();

    void rof$setExposureStamp(int value);

    float rof$getCachedExposure();

    void rof$setCachedExposure(float value);
}
