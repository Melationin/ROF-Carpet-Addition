package com.carpet.rof.rules.asyncWorldgen;

import java.util.concurrent.atomic.AtomicLong;

//测试用
public final class DebugStats
{
    public static final int DURATION_TICKS = 200;
    private static volatile Recording current;

    private DebugStats()
    {
    }

    public static synchronized boolean start()
    {
        if (current != null)
            return false;
        current = new Recording();
        return true;
    }

    public static synchronized Snapshot stop()
    {
        Recording recording = current;
        current = null;
        return recording == null ? null : recording.snapshot();
    }

    public static Recording current()
    {
        return current;
    }

    public static void recordRandomTickDecision(boolean usedAsync)
    {
        Recording recording = current;
        if (recording != null)
            recording.recordRandomTickDecision(usedAsync);
    }

    public static void recordNaturalSpawnDecision(boolean usedAsync)
    {
        Recording recording = current;
        if (recording != null)
            recording.recordNaturalSpawnDecision(usedAsync);
    }

    public static final class Recording
    {
        private final AtomicLong randomTickDecisions = new AtomicLong();
        private final AtomicLong randomTickAsyncUsed = new AtomicLong();
        private final AtomicLong randomTickReturnedChunks = new AtomicLong();
        private final AtomicLong randomTickReturnedResults = new AtomicLong();
        private final AtomicLong naturalSpawnDecisions = new AtomicLong();
        private final AtomicLong naturalSpawnAsyncUsed = new AtomicLong();
        private final AtomicLong naturalSpawnReturnedChunks = new AtomicLong();
        private final AtomicLong naturalSpawnReturnedAttempts = new AtomicLong();

        private void recordRandomTickDecision(boolean usedAsync)
        {
            if (current != this)
                return;
            randomTickDecisions.incrementAndGet();
            if (usedAsync)
                randomTickAsyncUsed.incrementAndGet();
        }

        private void recordNaturalSpawnDecision(boolean usedAsync)
        {
            if (current != this)
                return;
            naturalSpawnDecisions.incrementAndGet();
            if (usedAsync)
                naturalSpawnAsyncUsed.incrementAndGet();
        }

        public void recordRandomTickResult(int count)
        {
            if (current != this)
                return;
            randomTickReturnedChunks.incrementAndGet();
            randomTickReturnedResults.addAndGet(count);
        }

        public void recordNaturalSpawnResult(int count)
        {
            if (current != this)
                return;
            naturalSpawnReturnedChunks.incrementAndGet();
            naturalSpawnReturnedAttempts.addAndGet(count);
        }

        private Snapshot snapshot()
        {
            return new Snapshot(randomTickDecisions.get(), randomTickAsyncUsed.get(), randomTickReturnedChunks.get(),
                    randomTickReturnedResults.get(), naturalSpawnDecisions.get(), naturalSpawnAsyncUsed.get(),
                    naturalSpawnReturnedChunks.get(), naturalSpawnReturnedAttempts.get());
        }
    }

    public record Snapshot(long randomTickDecisions, long randomTickAsyncUsed, long randomTickReturnedChunks,
                           long randomTickReturnedResults, long naturalSpawnDecisions, long naturalSpawnAsyncUsed,
                           long naturalSpawnReturnedChunks, long naturalSpawnReturnedAttempts)
    {
        public double randomTickAsyncRatio()
        {
            return randomTickDecisions == 0 ? 0.0 : (double) randomTickAsyncUsed / randomTickDecisions;
        }

        public double randomTickResultsPerChunk()
        {
            return randomTickReturnedChunks == 0 ? 0.0 : (double) randomTickReturnedResults / randomTickReturnedChunks;
        }

        public double naturalSpawnAsyncRatio()
        {
            return naturalSpawnDecisions == 0 ? 0.0 : (double) naturalSpawnAsyncUsed / naturalSpawnDecisions;
        }

        public double naturalSpawnAttemptsPerChunk()
        {
            return naturalSpawnReturnedChunks == 0 ? 0.0 : (double) naturalSpawnReturnedAttempts / naturalSpawnReturnedChunks;
        }
    }
}
