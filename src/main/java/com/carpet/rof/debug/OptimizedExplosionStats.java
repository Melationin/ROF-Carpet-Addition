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
    private static long exposureComputed;
    private static long exposureReused;
    private static long exposureMissNew;
    private static long exposureMissMoved;
    private static long exposureMissGroup;
    private static long exposureMissIndex;
    private static long exposureUnmanaged;
    private static long entityQueries;
    private static long queriedEntities;

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
        exposureComputed = 0L;
        exposureReused = 0L;
        exposureMissNew = 0L;
        exposureMissMoved = 0L;
        exposureMissGroup = 0L;
        exposureMissIndex = 0L;
        exposureUnmanaged = 0L;
        entityQueries = 0L;
        queriedEntities = 0L;
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

    public static void onExposureComputed()
    {
        if (active) exposureComputed++;
    }

    public static void onExposureReused()
    {
        if (active) exposureReused++;
    }

    // 未命中：0 = 本组里从没算过，-1 = 算过之后动过，其余 = 上一个合并组留下的值
    public static void onExposureMiss(int stamp)
    {
        if (!active) return;
        if (stamp == 0) exposureMissNew++;
        else if (stamp < 0) exposureMissMoved++;
        else exposureMissGroup++;
    }

    public static void onExposureMissIndex()
    {
        if (active) exposureMissIndex++;
    }

    // 没走合并组、直接调原版 getSeenPercent 的次数
    public static void onExposureUnmanaged()
    {
        if (active) exposureUnmanaged++;
    }

    public static void onEntityQuery(int entities)
    {
        if (!active) return;
        entityQueries++;
        queriedEntities += entities;
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
        long exposures = exposureComputed + exposureReused;
        lines.add(String.format(Locale.ROOT,
                "  entity exposure: %d raycast sets, reused %d (%.2f%%), recomputed %d",
                exposures, exposureReused, percent(exposureReused, exposures), exposureComputed));
        lines.add(String.format(Locale.ROOT,
                "  entity queries: %d, entities seen %d (avg %.1f per explosion)",
                entityQueries, queriedEntities, entityQueries == 0L ? 0.0D : (double) queriedEntities / entityQueries));
        return lines;
    }

    public static List<String> exposureLines(long gameTime)
    {
        List<String> lines = new ArrayList<>();
        long misses = exposureMissNew + exposureMissMoved + exposureMissGroup + exposureMissIndex;
        long lookups = exposureReused + misses;
        lines.add(String.format(Locale.ROOT,
                "ROF exposure cache: %s, %d lookups in %d gt (%.2f s)",
                active ? "recording" : "stopped", lookups, gameTime - startTick, (System.currentTimeMillis() - startMillis) / 1000.0D));
        lines.add(String.format(Locale.ROOT,
                "  hit: %d (%.2f%%), miss: %d",
                exposureReused, percent(exposureReused, lookups), misses));
        lines.add(String.format(Locale.ROOT,
                "  miss reasons: never computed %d, moved since %d, stale group %d, invalid index %d",
                exposureMissNew, exposureMissMoved, exposureMissGroup, exposureMissIndex));
        lines.add(String.format(Locale.ROOT,
                "  recomputed %d, ran outside an optimized group %d",
                exposureComputed, exposureUnmanaged));
        return lines;
    }

    private static double percent(long value, long total)
    {
        return total == 0L ? 0.0D : value * 100.0D / total;
    }
}
