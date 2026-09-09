package com.carpet.rof.rules.oec.lithium;

/**
 * Per-block-state cache used by the Lithium entity pushability hot path.
 *
 * <p>The cache is versioned because block tags can change after a data-pack reload.</p>
 */
public interface ClimbableStateCacheAccess {
    int rof$getClimbableCacheEpoch();

    boolean rof$getClimbableCacheValue();

    void rof$setClimbableCache(int epoch, boolean value);
}
