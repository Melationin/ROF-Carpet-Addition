package com.carpet.rof.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TimeUtil;

import java.util.Arrays;
import java.util.Locale;

public final class SprintTickTimer
{
    private static final int MAX_SAMPLES = 1 << 20;
    private static CommandSourceStack source;
    private static long[] samples = new long[1024];
    private static int sampleCount;
    private static long completedTicks;
    private static long totalNanos;

    private SprintTickTimer()
    {
    }

    public static void start(CommandSourceStack commandSource)
    {
        source = commandSource;
        sampleCount = 0;
        completedTicks = 0L;
        totalNanos = 0L;
    }

    public static boolean isRunning()
    {
        return source != null;
    }

    public static void onTickEnd(long nanos)
    {
        if (source == null) return;
        completedTicks++;
        totalNanos += nanos;
        if (sampleCount < MAX_SAMPLES)
        {
            if (sampleCount == samples.length)
            {
                samples = Arrays.copyOf(samples, Math.min(samples.length * 2, MAX_SAMPLES));
            }
            samples[sampleCount++] = nanos;
        }

        double milliseconds = nanos / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        long tick = completedTicks;
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "[ROF] gt %d: %.2fms", tick, milliseconds)), false);
    }

    public static void finish()
    {
        if (source == null) return;
        CommandSourceStack finishedSource = source;
        source = null;
        long ticks = completedTicks;
        long total = totalNanos;
        long[] sorted = Arrays.copyOf(samples, sampleCount);
        completedTicks = 0L;
        totalNanos = 0L;
        sampleCount = 0;
        if (ticks == 0L) return;

        double average = total / (double) ticks / TimeUtil.NANOSECONDS_PER_MILLISECOND;
        double totalMilliseconds = Math.max(1.0, total / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND);
        int ticksPerSecond = (int) (TimeUtil.MILLISECONDS_PER_SECOND * ticks / totalMilliseconds);
        if (sorted.length == 0)
        {
            finishedSource.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                    "[ROF] sprint ticks: %d | avg %.2fms | ~%d tps", ticks, average, ticksPerSecond)), false);
            return;
        }

        Arrays.sort(sorted);
        double p50 = sorted[sorted.length / 2] / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        double p95 = sorted[(int)(sorted.length * 0.95)] / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        double p99 = sorted[(int)(sorted.length * 0.99)] / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        double max = sorted[sorted.length - 1] / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        finishedSource.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                "[ROF] sprint ticks: %d | avg %.2fms | p50 %.2f | p95 %.2f | p99 %.2f | max %.2f | ~%d tps",
                ticks, average, p50, p95, p99, max, ticksPerSecond)), false);
    }
}
