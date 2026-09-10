package com.carpet.rof.rules.oec;

import java.util.ArrayList;
import java.util.Arrays;

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
        frame.wordCount = 0;
        frame.acquired = true;
        return frame;
    }

    // 仅在 collectCandidateSlots 完成后读取 [0, wordCount())。
    public long[] words() {
        return this.words;
    }

    public int wordCount() {
        return this.wordCount;
    }

    public int cardinality() {
        int sum = 0;
        for (int i = 0; i < this.wordCount; i++) sum += Long.bitCount(this.words[i]);
        return sum;
    }

    long[] resetWords(int wordCount) {
        if (this.words.length < wordCount) this.words = new long[Math.max(wordCount, this.words.length << 1)];
        else Arrays.fill(this.words, 0, wordCount, 0L);
        this.wordCount = wordCount;
        return this.words;
    }

    void fillAll(int size) {
        int words = (size + 63) >>> 6;
        long[] target = resetWords(words);
        Arrays.fill(target, 0, words, -1L);
        int remainder = size & 63;
        if (remainder != 0) target[words - 1] &= (1L << remainder) - 1L;
    }

    @Override
    public void close() {
        if (!this.acquired) return;
        this.acquired = false;
        this.wordCount = 0;
        this.pool.depth--;
    }
    private static final class Pool { private final ArrayList<OecQueryFrame> frames = new ArrayList<>(2); private int depth; }
}
