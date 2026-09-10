package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;

// 仅新增 section 时失效；消费缓存时仍检查 section 是否存在。
public final class OecSectionSpanCache
{
    private static final int SLOTS = 4;
    private static final int DIMENSIONS = 6;
    private static final int MAX_KEYS = 64;
    private static final long[] EMPTY = new long[0];

    private final long[][] keys = new long[SLOTS][];
    private final int[] coords = new int[SLOTS * DIMENSIONS];
    private long[] scratch = new long[8];
    private int next;

    // null 表示回退原版枚举，空数组表示有效的空结果。
    public long[] get(OecSectionStorageAccess storage, AABB box)
    {
        if (!isFinite(box)) return null;
        int xMin = SectionPos.posToSectionCoord(box.minX - 2.0);
        int xMax = SectionPos.posToSectionCoord(box.maxX + 2.0);
        int yMin = SectionPos.posToSectionCoord(box.minY - 4.0);
        int yMax = SectionPos.posToSectionCoord(box.maxY + 0.0);
        int zMin = SectionPos.posToSectionCoord(box.minZ - 2.0);
        int zMax = SectionPos.posToSectionCoord(box.maxZ + 2.0);

        for (int slot = 0; slot < SLOTS; slot++) {
            long[] entry = this.keys[slot];
            if (entry == null) continue;
            int base = slot * DIMENSIONS;
            if (this.coords[base] == xMin && this.coords[base + 1] == xMax
                    && this.coords[base + 2] == yMin && this.coords[base + 3] == yMax
                    && this.coords[base + 4] == zMin && this.coords[base + 5] == zMax) {
                if (OecMetrics.ENABLED) OecMetrics.SPAN_HITS.increment();
                return entry;
            }
        }

        long[] built = this.build(storage, xMin, xMax, yMin, yMax, zMin, zMax);
        if (built == null) {
            if (OecMetrics.ENABLED) OecMetrics.SPAN_SKIPPED.increment();
            return null;
        }

        int slot = -1;
        for (int i = 0; i < SLOTS; i++) {
            if (this.keys[i] == null) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            slot = this.next;
            this.next = (this.next + 1) & (SLOTS - 1);
        }
        this.keys[slot] = built;
        int base = slot * DIMENSIONS;
        this.coords[base] = xMin;
        this.coords[base + 1] = xMax;
        this.coords[base + 2] = yMin;
        this.coords[base + 3] = yMax;
        this.coords[base + 4] = zMin;
        this.coords[base + 5] = zMax;
        if (OecMetrics.ENABLED) OecMetrics.SPAN_MISSES.increment();
        return built;
    }

    public void invalidateIfContains(long sectionKey)
    {
        int x = SectionPos.x(sectionKey);
        int y = SectionPos.y(sectionKey);
        int z = SectionPos.z(sectionKey);
        for (int slot = 0; slot < SLOTS; slot++) {
            if (this.keys[slot] == null) continue;
            int base = slot * DIMENSIONS;
            if (x >= this.coords[base] && x <= this.coords[base + 1]
                    && y >= this.coords[base + 2] && y <= this.coords[base + 3]
                    && z >= this.coords[base + 4] && z <= this.coords[base + 5]) {
                this.keys[slot] = null;
                if (OecMetrics.ENABLED) OecMetrics.SPAN_INVALIDATED.increment();
            }
        }
    }

    private long[] build(OecSectionStorageAccess storage, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax)
    {
        LongSortedSet ids = storage.rof$sectionIds();
        long[] buffer = this.scratch;
        int count = 0;
        for (int x = xMin; x <= xMax; x++) {
            LongIterator iterator = ids.subSet(SectionPos.asLong(x, 0, 0), SectionPos.asLong(x, -1, -1) + 1L).iterator();
            while (iterator.hasNext()) {
                long key = iterator.nextLong();
                int y = SectionPos.y(key);
                int z = SectionPos.z(key);
                if (y < yMin || y > yMax || z < zMin || z > zMax) continue;
                if (count == MAX_KEYS) return null;
                if (count == buffer.length) {
                    buffer = this.scratch = Arrays.copyOf(buffer, Math.min(MAX_KEYS, buffer.length << 1));
                }
                buffer[count++] = key;
            }
        }
        return count == 0 ? EMPTY : Arrays.copyOf(buffer, count);
    }

    private static boolean isFinite(AABB box)
    {
        return Double.isFinite(box.minX) && Double.isFinite(box.minY) && Double.isFinite(box.minZ)
                && Double.isFinite(box.maxX) && Double.isFinite(box.maxY) && Double.isFinite(box.maxZ);
    }
}
