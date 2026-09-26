package com.carpet.rof.rules.oec.lithium;


public interface ClimbableStateCacheAccess
{
    int rof$getClimbableCacheEpoch();

    boolean rof$getClimbableCacheValue();

    void rof$setClimbableCache(int epoch, boolean value);
}
