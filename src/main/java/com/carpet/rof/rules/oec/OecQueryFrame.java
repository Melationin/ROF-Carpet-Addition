package com.carpet.rof.rules.oec;

import java.util.ArrayList;
import java.util.BitSet;

/** Re-entrant per-thread scratch storage for spatial queries. */
public final class OecQueryFrame implements AutoCloseable {
    private static final ThreadLocal<Pool> POOL = ThreadLocal.withInitial(Pool::new);
    private final Pool pool;
    private final BitSet bits = new BitSet();
    private boolean acquired;
    private OecQueryFrame(Pool pool) { this.pool = pool; }
    public static OecQueryFrame acquire() {
        Pool pool = POOL.get();
        if (pool.depth == pool.frames.size()) pool.frames.add(new OecQueryFrame(pool));
        OecQueryFrame frame = pool.frames.get(pool.depth++);
        frame.bits.clear(); frame.acquired = true;
        return frame;
    }
    public BitSet bits() { return this.bits; }
    @Override public void close() {
        if (!this.acquired) return;
        this.acquired = false; this.bits.clear(); this.pool.depth--;
    }
    private static final class Pool { private final ArrayList<OecQueryFrame> frames = new ArrayList<>(2); private int depth; }
}
