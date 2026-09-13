package com.carpet.rof.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 爆炸优化规则的效果统计：记录一段时间内被跳过的爆炸占全部爆炸的比例。
 * 未开启统计时，所有计数只多一次静态布尔读取。
 */
public final class OptimizedExplosionStats
{
    private static boolean active;
    private static long startTick;
    private static long startMillis;

    private static long total;
    private static long skipped;
    private static long cachedVerdict;
    private static long dryRuns;
    private static long unsafeVerdict;
    private static long belowThreshold;
    private static long forceStopped;
    private static long unavailable;

    private OptimizedExplosionStats() {}

    public static boolean isActive()
    {
        return active;
    }

    public static void start(long gameTime)
    {
        active = true;
        startTick = gameTime;
        startMillis = System.currentTimeMillis();
        clearCounters();
    }

    public static void stop()
    {
        active = false;
    }

    public static void reset()
    {
        startTick = 0L;
        startMillis = System.currentTimeMillis();
        clearCounters();
    }

    private static void clearCounters()
    {
        total = 0L;
        skipped = 0L;
        cachedVerdict = 0L;
        dryRuns = 0L;
        unsafeVerdict = 0L;
        belowThreshold = 0L;
        forceStopped = 0L;
        unavailable = 0L;
    }

    public static void onExplosion()
    {
        if (active) total++;
    }

    public static void onSkipped()
    {
        if (active) skipped++;
    }

    public static void onCachedVerdict()
    {
        if (active) cachedVerdict++;
    }

    public static void onDryRun()
    {
        if (active) dryRuns++;
    }

    public static void onUnsafeVerdict()
    {
        if (active) unsafeVerdict++;
    }

    public static void onBelowThreshold()
    {
        if (active) belowThreshold++;
    }

    public static void onForceStopped()
    {
        if (active) forceStopped++;
    }

    public static void onUnavailable()
    {
        if (active) unavailable++;
    }

    public static List<String> lines(long gameTime)
    {
        List<String> lines = new ArrayList<>();
        long unoptimized = unavailable + belowThreshold + unsafeVerdict + forceStopped;
        lines.add(String.format(Locale.ROOT,
                "ROF optimizedExplosion: %s, %d explosions in %d gt (%.2f s)",
                active ? "recording" : "stopped", total, gameTime - startTick, (System.currentTimeMillis() - startMillis) / 1000.0D));
        lines.add(String.format(Locale.ROOT,
                "  skipped block raycast: %d (%.2f%%, %.1f per 1000)",
                skipped, percent(skipped, total), total == 0 ? 0.0D : skipped * 1000.0D / total));
        lines.add(String.format(Locale.ROOT,
                "  not optimized: %d  [below threshold %d, unsafe verdict %d, already stopped %d, unavailable %d]",
                unoptimized, belowThreshold, unsafeVerdict, forceStopped, unavailable));
        lines.add(String.format(Locale.ROOT,
                "  worst case dry runs: %d (each one costs a full block raycast), skipped by cached verdict: %d",
                dryRuns, cachedVerdict));
        return lines;
    }

    private static double percent(long value, long total)
    {
        return total == 0L ? 0.0D : value * 100.0D / total;
    }
}
