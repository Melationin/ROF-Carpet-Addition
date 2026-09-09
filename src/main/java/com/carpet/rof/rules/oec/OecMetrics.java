package com.carpet.rof.rules.oec;

import com.carpet.rof.utils.ROFTool;

import java.util.concurrent.atomic.LongAdder;

public final class OecMetrics {
    /** Counters are only maintained for the debug tooling; the JIT folds this constant away in production builds. */
    public static final boolean ENABLED = ROFTool.DEBUG;
    public static final LongAdder QUERIES = new LongAdder(), GRID_QUERIES = new LongAdder(), LITHIUM_FALLBACKS = new LongAdder();
    public static final LongAdder CANDIDATES = new LongAdder(), EXACT_HITS = new LongAdder(), PREDICATE_CALLS = new LongAdder(), REBUILDS = new LongAdder();
    public static final LongAdder BOUNDS_UPDATES = new LongAdder(), RANGE_CHANGES = new LongAdder();
    private OecMetrics() {}

    public static String summary() {
        return "queries=" + QUERIES.sum() + ", grid=" + GRID_QUERIES.sum() + ", fallback=" + LITHIUM_FALLBACKS.sum()
                + ", candidates=" + CANDIDATES.sum() + ", exact=" + EXACT_HITS.sum() + ", predicates=" + PREDICATE_CALLS.sum()
                + ", boundsUpdates=" + BOUNDS_UPDATES.sum() + ", rangeChanges=" + RANGE_CHANGES.sum()
                + ", rebuilds=" + REBUILDS.sum() + ", activeGrids=" + OecGridRegistry.activeCount();
    }

    public static void reset() {
        QUERIES.reset(); GRID_QUERIES.reset(); LITHIUM_FALLBACKS.reset(); CANDIDATES.reset();
        EXACT_HITS.reset(); PREDICATE_CALLS.reset(); REBUILDS.reset(); BOUNDS_UPDATES.reset(); RANGE_CHANGES.reset();
    }
}
