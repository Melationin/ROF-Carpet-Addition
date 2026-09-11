package com.carpet.rof.rules.oec;

import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/** 实体移动时只把"新接触的 cell"写进自己 section 的网格，脱离的 cell 留给查询时的实时 AABB 淘汰。 */
public final class OecRegistration {
    private OecRegistration() {}

    public static void registerNewCells(Entity entity) {
        OecEntityAccess access = (OecEntityAccess) entity;
        OecSectionAccess section = access.rof$entitySection();
        if (section == null) return;
        SectionEntityGrid grid = section.rof$grid();
        if (grid == null || !grid.isValid()) return;
        long key = section.rof$sectionKey();
        if (key == Long.MIN_VALUE) return;
        AABB box = entity.getBoundingBox();
        if (!OecCells.isFinite(box)) return;

        int sectionCellX = SectionPos.x(key) << 3;
        int sectionCellY = SectionPos.y(key) << 3;
        int sectionCellZ = SectionPos.z(key) << 3;
        int minX = Math.max(OecCells.cell(box.minX), sectionCellX);
        int maxX = Math.min(OecCells.cell(box.maxX), sectionCellX + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        int minY = Math.max(OecCells.cell(box.minY), sectionCellY);
        int maxY = Math.min(OecCells.cell(box.maxY), sectionCellY + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        int minZ = Math.max(OecCells.cell(box.minZ), sectionCellZ);
        int maxZ = Math.min(OecCells.cell(box.maxZ), sectionCellZ + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        if (minX > maxX || minY > maxY || minZ > maxZ) return;

        int old = access.rof$registeredCells();
        int range = OecCells.packRange(minX & 7, minY & 7, minZ & 7, maxX & 7, maxY & 7, maxZ & 7);
        if (range == old) return;

        boolean pushable = access.rof$pushableBit();
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    if (old != OecCells.EMPTY_RANGE && OecCells.rangeContains(old, x & 7, y & 7, z & 7)) continue;
                    grid.add(grid.localCell(x, y, z), entity, pushable);
                    if (OecMetrics.ENABLED) OecMetrics.BOUNDS_UPDATES.increment();
                }
            }
        }
        access.rof$setRegisteredCells(range);
        if (OecMetrics.ENABLED) OecMetrics.RANGE_CHANGES.increment();
    }
}
