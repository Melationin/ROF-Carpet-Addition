package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;

/** 每 tick 全局重建：先清空所有 cell，再把实体按实时 AABB 写进自己与邻居 section 的 cell。 */
public final class OecGridRebuilder {
    private static final int FINE_THRESHOLD = 64;
    private static final int COARSE_THRESHOLD = 32;
    private static long cachedNeighborKey = Long.MIN_VALUE;
    private static OecSectionAccess cachedNeighbor;

    private OecGridRebuilder() {}

    public static void rebuild(ServerLevel level) {
        EntitySectionStorage<?> storage = OecStorageHolder.storageOf(level);
        if (!(storage instanceof OecSectionStorageAccess storageAccess)) return;

        for (LongIterator iterator = storageAccess.rof$sectionIds().iterator(); iterator.hasNext(); ) {
            if (storage.getSection(iterator.nextLong()) instanceof OecSectionAccess section) section.rof$clearCells();
        }
        if (OecMetrics.ENABLED) OecMetrics.REBUILDS.increment();

        for (LongIterator iterator = storageAccess.rof$sectionIds().iterator(); iterator.hasNext(); ) {
            long key = iterator.nextLong();
            if (!(storage.getSection(key) instanceof OecSectionAccess section)) continue;
            int count = section.rof$entityCount();
            SectionEntityGrid grid = section.rof$grid();
            if (grid == null) {
                section.rof$ensureGrid(count >= FINE_THRESHOLD);
            } else if (grid.isFine() ? count <= COARSE_THRESHOLD : count >= FINE_THRESHOLD) {
                section.rof$ensureGrid(!grid.isFine());
            }
            fillSection(storage, key, section);
        }
        cachedNeighborKey = Long.MIN_VALUE;
        cachedNeighbor = null;
    }

    private static void fillSection(EntitySectionStorage<?> storage, long key, OecSectionAccess section) {
        int sectionX = SectionPos.x(key);
        int sectionY = SectionPos.y(key);
        int sectionZ = SectionPos.z(key);
        int baseCellX = sectionX << 3;
        int baseCellY = sectionY << 3;
        int baseCellZ = sectionZ << 3;

        for (Entity entity : section.rof$entities()) {
            if (!OecPushableFilter.maybePushable(entity)) continue;
            AABB box = entity.getBoundingBox();
            if (!OecCells.isFinite(box)) continue;
            boolean pushable = OecPushableFilter.currentlyPushable(entity);
            OecEntityAccess access = (OecEntityAccess) entity;
            access.rof$setPushableBit(pushable);

            int minX = OecCells.cell(box.minX);
            int maxX = OecCells.cell(box.maxX);
            int minY = OecCells.cell(box.minY);
            int maxY = OecCells.cell(box.maxY);
            int minZ = OecCells.cell(box.minZ);
            int maxZ = OecCells.cell(box.maxZ);

            int ownMinX = Math.max(minX, baseCellX);
            int ownMaxX = Math.min(maxX, baseCellX + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            int ownMinY = Math.max(minY, baseCellY);
            int ownMaxY = Math.min(maxY, baseCellY + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            int ownMinZ = Math.max(minZ, baseCellZ);
            int ownMaxZ = Math.min(maxZ, baseCellZ + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            access.rof$setRegisteredCells(ownMinX <= ownMaxX && ownMinY <= ownMaxY && ownMinZ <= ownMaxZ
                    ? OecCells.packRange(ownMinX & 7, ownMinY & 7, ownMinZ & 7, ownMaxX & 7, ownMaxY & 7, ownMaxZ & 7)
                    : OecCells.EMPTY_RANGE);

            for (int cellY = minY; cellY <= maxY; cellY++) {
                int neighborY = cellY >> 3;
                for (int cellZ = minZ; cellZ <= maxZ; cellZ++) {
                    int neighborZ = cellZ >> 3;
                    for (int cellX = minX; cellX <= maxX; cellX++) {
                        int neighborX = cellX >> 3;
                        OecSectionAccess target = neighborX == sectionX && neighborY == sectionY && neighborZ == sectionZ
                                ? section
                                : neighbor(storage, neighborX, neighborY, neighborZ);
                        if (target == null) continue;
                        SectionEntityGrid grid = target.rof$grid();
                        if (grid == null) continue;
                        grid.add(grid.localCell(cellX, cellY, cellZ), entity, pushable);
                        if (OecMetrics.ENABLED) OecMetrics.REGISTRATIONS.increment();
                    }
                }
            }
        }
    }

    private static OecSectionAccess neighbor(EntitySectionStorage<?> storage, int sectionX, int sectionY, int sectionZ) {
        long key = SectionPos.asLong(sectionX, sectionY, sectionZ);
        if (key != cachedNeighborKey) {
            EntitySection<?> section = storage.getSection(key);
            cachedNeighborKey = key;
            cachedNeighbor = section instanceof OecSectionAccess access ? access : null;
        }
        return cachedNeighbor;
    }
}
