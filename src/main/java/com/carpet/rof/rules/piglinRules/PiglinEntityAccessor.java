package com.carpet.rof.rules.piglinRules;

public interface PiglinEntityAccessor {
    boolean rof$isRegularAiActive();

    long rof$getNextGroupRefreshTick();

    void rof$setOptimizationState(boolean regularAiActive, long nextGroupRefreshTick);
}
