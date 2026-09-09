package com.carpet.rof.rules.oec.lithium;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Owns the generation used to invalidate cached block-tag membership.
 */
public final class ClimbableBlockStateCache {
    private static volatile int epoch = 1;

    private ClimbableBlockStateCache() {
    }

    public static void registerLifecycleEvents() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                invalidate();
            }
        });
    }

    public static int epoch() {
        return epoch;
    }

    public static void invalidate() {
        int next = epoch + 1;
        epoch = next == 0 ? 1 : next;
    }
}
