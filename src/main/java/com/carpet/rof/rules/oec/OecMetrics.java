package com.carpet.rof.rules.oec;

import com.carpet.rof.utils.ROFTool;

import java.util.concurrent.atomic.LongAdder;

public final class OecMetrics {
    public static final boolean ENABLED = ROFTool.DEBUG;
    public static final LongAdder QUERIES = new LongAdder(), GRID_QUERIES = new LongAdder(), LITHIUM_FALLBACKS = new LongAdder();
    public static final LongAdder CANDIDATES = new LongAdder(), EXACT_HITS = new LongAdder(), PREDICATE_CALLS = new LongAdder(), REBUILDS = new LongAdder();
    public static final LongAdder REGISTRATIONS = new LongAdder(), BOUNDS_UPDATES = new LongAdder(), RANGE_CHANGES = new LongAdder();
    public static final LongAdder SPAN_HITS = new LongAdder(), SPAN_MISSES = new LongAdder(), SPAN_INVALIDATED = new LongAdder(), SPAN_SKIPPED = new LongAdder();
    private OecMetrics() {}

    public static String summary() {
        return "queries=" + QUERIES.sum() + ", grid=" + GRID_QUERIES.sum() + ", fallback=" + LITHIUM_FALLBACKS.sum()
                + ", candidates=" + CANDIDATES.sum() + ", exact=" + EXACT_HITS.sum() + ", predicates=" + PREDICATE_CALLS.sum()
                + ", registrations=" + REGISTRATIONS.sum()
                + ", boundsUpdates=" + BOUNDS_UPDATES.sum() + ", rangeChanges=" + RANGE_CHANGES.sum()
                + ", rebuildTicks=" + REBUILDS.sum() + ", activeGrids=" + OecGridRegistry.activeCount()
                + ", spanHits=" + SPAN_HITS.sum() + ", spanMisses=" + SPAN_MISSES.sum()
                + ", spanInvalidated=" + SPAN_INVALIDATED.sum() + ", spanSkipped=" + SPAN_SKIPPED.sum();
    }

    public static void reset() {
        QUERIES.reset(); GRID_QUERIES.reset(); LITHIUM_FALLBACKS.reset(); CANDIDATES.reset();
        EXACT_HITS.reset(); PREDICATE_CALLS.reset(); REBUILDS.reset(); REGISTRATIONS.reset();
        BOUNDS_UPDATES.reset(); RANGE_CHANGES.reset();
        SPAN_HITS.reset();
        SPAN_MISSES.reset();
        SPAN_INVALIDATED.reset();
        SPAN_SKIPPED.reset();
    }
}
