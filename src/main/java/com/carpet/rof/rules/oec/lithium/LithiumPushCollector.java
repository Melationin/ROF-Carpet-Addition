package com.carpet.rof.rules.oec.lithium;

import net.caffeinemc.mods.lithium.common.entity.pushable.EntityPushablePredicate;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.AABB;
import com.carpet.rof.rules.oec.OecCells;
import com.carpet.rof.rules.oec.OecEntityAccess;
import com.carpet.rof.rules.oec.OecMetrics;
import com.carpet.rof.rules.oec.OecQueryStamp;
import com.carpet.rof.rules.oec.OecSectionAccess;
import com.carpet.rof.rules.oec.SectionEntityGrid;

import java.util.ArrayList;

public final class LithiumPushCollector {
    private LithiumPushCollector() {}

    /** 返回 false 时调用方必须回退 Lithium 的原收集器。 */
    public static boolean tryCollect(Object section, Entity except, AABB box,
                                     EntityPushablePredicate<? super Entity> predicate, ArrayList<Entity> output) {
        if (!(section instanceof OecSectionAccess sectionAccess)) return false;
        SectionEntityGrid grid = sectionAccess.rof$grid();
        if (grid == null || !grid.isValid()) return false;
        long key = sectionAccess.rof$sectionKey();
        if (key == Long.MIN_VALUE) return false;
        if (OecMetrics.ENABLED) OecMetrics.GRID_QUERIES.increment();

        int baseCellX = SectionPos.x(key) << 3;
        int baseCellY = SectionPos.y(key) << 3;
        int baseCellZ = SectionPos.z(key) << 3;
        int lastCell = SectionEntityGrid.SECTION_CELL_COUNT - 1;
        int minX = Math.max(OecCells.cell(box.minX), baseCellX);
        int maxX = Math.min(OecCells.cell(box.maxX), baseCellX + lastCell);
        int minY = Math.max(OecCells.cell(box.minY), baseCellY);
        int maxY = Math.min(OecCells.cell(box.maxY), baseCellY + lastCell);
        int minZ = Math.max(OecCells.cell(box.minZ), baseCellZ);
        int maxZ = Math.min(OecCells.cell(box.maxZ), baseCellZ + lastCell);
        if (minX > maxX || minY > maxY || minZ > maxZ) return true;

        long stamp = OecQueryStamp.current();
        boolean metrics = OecMetrics.ENABLED;
        int lastScannedCell = -1;
        for (int cellY = minY; cellY <= maxY; cellY++) {
            for (int cellZ = minZ; cellZ <= maxZ; cellZ++) {
                for (int cellX = minX; cellX <= maxX; cellX++) {
                    int cell = grid.localCell(cellX, cellY, cellZ);
                    if (cell == lastScannedCell) continue;
                    lastScannedCell = cell;
                    Entity[] entities = grid.cellEntities(cell);
                    if (entities == null) continue;
                    int size = grid.cellSize(cell);
                    long[] mask = grid.cellMask(cell);
                    // 逐字 + trailing zeros 只走置位条目，等价于 BitSet.nextSetBit
                    int wordCount = (size + 63) >>> 6;
                    for (int word = 0, base = 0; word < wordCount; word++, base += 64) {
                        long bits = mask[word];
                        int remaining = size - base;
                        if (remaining < 64) bits &= (1L << remaining) - 1L;
                        while (bits != 0L) {
                            Entity entity = entities[base + Long.numberOfTrailingZeros(bits)];
                            bits &= bits - 1L;
                            if (entity == null) continue;
                            OecEntityAccess access = (OecEntityAccess) entity;
                            if (access.rof$pushStamp() == stamp) continue;
                            access.rof$setPushStamp(stamp);
                            if (metrics) OecMetrics.CANDIDATES.increment();
                            if (entity == except || entity.isSpectator() || entity instanceof EnderDragon) continue;
                            if (!entity.getBoundingBox().intersects(box)) continue;
                            if (metrics) {
                                OecMetrics.EXACT_HITS.increment();
                                OecMetrics.PREDICATE_CALLS.increment();
                            }
                            if (predicate.test(entity)) output.add(entity);
                        }
                    }
                }
            }
        }
        return true;
    }
}
