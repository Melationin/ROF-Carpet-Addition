package com.carpet.rof.rules.oec;

import java.util.ArrayList;
import java.util.Arrays;

/** Re-entrant per-thread scratch storage for spatial queries: a reusable word buffer holding the candidate slot set. */
public final class OecQueryFrame implements AutoCloseable {
    private static final ThreadLocal<Pool> POOL = ThreadLocal.withInitial(Pool::new);
    private final Pool pool;
    private long[] words = new long[1];
    private int wordCount;
    private boolean acquired;
    private OecQueryFrame(Pool pool) { this.pool = pool; }
    public static OecQueryFrame acquire() {
        Pool pool = POOL.get();
        if (pool.depth == pool.frames.size()) pool.frames.add(new OecQueryFrame(pool));
        OecQueryFrame frame = pool.frames.get(pool.depth++);
        frame.wordCount = 0; frame.acquired = true;
        return frame;
    }
    /** Candidate bits, valid for words [0, wordCount()). Read it only after collectCandidateSlots returned. */
    public long[] words() { return this.words; }
    public int wordCount() { return this.wordCount; }
    /** Candidate count; only used by the debug metrics. */
    public int cardinality() {
        int sum = 0;
        for (int i = 0; i < this.wordCount; i++) sum += Long.bitCount(this.words[i]);
        return sum;
    }
    /** Returns a zeroed buffer of wordCount words and publishes it as the current candidate set. */
    long[] resetWords(int wordCount) {
        if (this.words.length < wordCount) this.words = new long[Math.max(wordCount, this.words.length << 1)];
        else Arrays.fill(this.words, 0, wordCount, 0L);
        this.wordCount = wordCount;
        return this.words;
    }
    /** Candidate set containing every slot in [0, size). */
    void fillAll(int size) {
        int words = (size + 63) >>> 6;
        long[] target = resetWords(words);
        Arrays.fill(target, 0, words, -1L);
        int remainder = size & 63;
        if (remainder != 0) target[words - 1] &= (1L << remainder) - 1L;
    }
    @Override public void close() {
        if (!this.acquired) return;
        this.acquired = false; this.wordCount = 0; this.pool.depth--;
    }
    private static final class Pool { private final ArrayList<OecQueryFrame> frames = new ArrayList<>(2); private int depth; }
}
