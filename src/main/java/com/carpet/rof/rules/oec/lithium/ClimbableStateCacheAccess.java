package com.carpet.rof.rules.oec.lithium;

// 数据包重载可能改变标签，缓存须按 epoch 失效。
public interface ClimbableStateCacheAccess
{
    int rof$getClimbableCacheEpoch();

    boolean rof$getClimbableCacheValue();

    void rof$setClimbableCache(int epoch, boolean value);
}
