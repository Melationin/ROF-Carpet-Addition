package com.carpet.rof.utils.asyncWorldgen;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public final class AsyncExecutor
{
    private static final int QUEUE_CAPACITY = 2;
    private static volatile ThreadPoolExecutor randomTick;
    private static volatile ThreadPoolExecutor spawning;

    private AsyncExecutor()
    {
    }

    public static synchronized void start()
    {
        stop();
        randomTick = create("rof-random-tick-worker");
        spawning = create("rof-natural-spawn-worker");
    }

    public static synchronized void stop()
    {
        shutdown(randomTick);
        shutdown(spawning);
        randomTick = null;
        spawning = null;
    }

    public static void submitRandomTick(Runnable task)
    {
        submit(randomTick, task);
    }

    public static void submitSpawning(Runnable task)
    {
        submit(spawning, task);
    }

    private static ThreadPoolExecutor create(String name)
    {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                runnable ->
                {
                    Thread thread = new Thread(runnable, name);
                    thread.setDaemon(true);
                    return thread;
                }, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private static void submit(ThreadPoolExecutor executor, Runnable task)
    {
        if (executor == null || executor.isShutdown())
            return;
        try {
            executor.execute(task);
        } catch (RejectedExecutionException ignored) {
        }
    }

    private static void shutdown(ThreadPoolExecutor executor)
    {
        if (executor != null)
            executor.shutdownNow();
    }
}
