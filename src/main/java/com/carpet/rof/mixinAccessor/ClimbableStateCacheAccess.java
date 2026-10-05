package com.carpet.rof.mixinAccessor;


public interface ClimbableStateCacheAccess
{
    int rof$getClimbableCacheEpoch();

    boolean rof$getClimbableCacheValue();

    void rof$setClimbableCache(int epoch, boolean value);
}
