package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.longs.LongIterator;
import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/** cell 坐标换算、候选筛选、实体移动时的增量登记，以及每 tick 的全局重建。 */
public final class OecUtil {
    public static final int EMPTY_RANGE = -1;
    private static final int RANGE_MASK = 7;
    private static final int FINE_THRESHOLD = 64;
    private static final int COARSE_THRESHOLD = 32;

    private static long stamp = 1L;
    private static long cachedNeighborKey = Long.MIN_VALUE;
    private static @Nullable OecSectionAccess cachedNeighbor;

    private OecUtil() {}

    public static void nextStamp() { stamp++; }

    public static long stamp() { return stamp; }

    public static int cell(double coordinate) {
        return (int) Math.floor(coordinate / SectionEntityGrid.CELL_SIZE);
    }

    public static int packRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return minX | (minY << 3) | (minZ << 6) | (maxX << 9) | (maxY << 12) | (maxZ << 15);
    }

    public static boolean rangeContains(int range, int x, int y, int z) {
        return x >= (range & RANGE_MASK) && x <= ((range >>> 9) & RANGE_MASK)
                && y >= ((range >>> 3) & RANGE_MASK) && y <= ((range >>> 12) & RANGE_MASK)
                && z >= ((range >>> 6) & RANGE_MASK) && z <= ((range >>> 15) & RANGE_MASK);
    }

    public static boolean isFinite(AABB box) {
        return Double.isFinite(box.minX) && Double.isFinite(box.minY) && Double.isFinite(box.minZ)
                && Double.isFinite(box.maxX) && Double.isFinite(box.maxY) && Double.isFinite(box.maxZ);
    }

    /** 与 Lithium 的 MAYBE_PUSHABLE 同义：Entity.isPushable 的默认实现返回 false，重写者才可能可推。 */
    public static boolean maybePushable(Entity entity) {
        return PushableEntityClassGroup.MAYBE_PUSHABLE.contains(entity);
    }

    /** 与 Lithium 的 entityPushableHeuristic 同源：身处可攀爬方块内视为不可推，其余情况交给谓词判定。 */
    public static boolean currentlyPushable(Entity entity) {
        return !PushableEntityClassGroup.CACHABLE_UNPUSHABILITY.contains(entity)
                || !entity.getInBlockState().is(BlockTags.CLIMBABLE);
    }

    public static @Nullable EntitySectionStorage<?> storageOf(Level level) {
        return level instanceof OecStorageHolder holder ? holder.rof$entitySectionStorage() : null;
    }

    /** 实体移动时只把"新接触的 cell"写进自己 section 的网格，脱离的 cell 留给查询时的实时 AABB 淘汰。 */
    public static void registerNewCells(Entity entity) {
        OecEntityAccess access = (OecEntityAccess) entity;
        OecSectionAccess section = access.rof$entitySection();
        if (section == null) return;
        SectionEntityGrid grid = section.rof$grid();
        if (grid == null || !grid.isValid()) return;
        long key = section.rof$sectionKey();
        if (key == Long.MIN_VALUE) return;
        AABB box = entity.getBoundingBox();
        if (!isFinite(box)) return;

        int sectionCellX = SectionPos.x(key) << 3;
        int sectionCellY = SectionPos.y(key) << 3;
        int sectionCellZ = SectionPos.z(key) << 3;
        int minX = Math.max(cell(box.minX), sectionCellX);
        int maxX = Math.min(cell(box.maxX), sectionCellX + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        int minY = Math.max(cell(box.minY), sectionCellY);
        int maxY = Math.min(cell(box.maxY), sectionCellY + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        int minZ = Math.max(cell(box.minZ), sectionCellZ);
        int maxZ = Math.min(cell(box.maxZ), sectionCellZ + SectionEntityGrid.SECTION_CELL_COUNT - 1);
        if (minX > maxX || minY > maxY || minZ > maxZ) return;

        int old = access.rof$registeredCells();
        int range = packRange(minX & 7, minY & 7, minZ & 7, maxX & 7, maxY & 7, maxZ & 7);
        if (range == old) return;

        boolean pushable = access.rof$pushableBit();
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    if (old != EMPTY_RANGE && rangeContains(old, x & 7, y & 7, z & 7)) continue;
                    grid.add(grid.localCell(x, y, z), entity, pushable);
                    if (OecMetrics.ENABLED) OecMetrics.BOUNDS_UPDATES.increment();
                }
            }
        }
        access.rof$setRegisteredCells(range);
        if (OecMetrics.ENABLED) OecMetrics.RANGE_CHANGES.increment();
    }

    /** 每 tick 全局重建：先清空所有 cell，再把实体按实时 AABB 写进自己与邻居 section 的 cell。 */
    public static void rebuild(ServerLevel level) {
        EntitySectionStorage<?> storage = storageOf(level);
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
            if (!maybePushable(entity)) continue;
            AABB box = entity.getBoundingBox();
            if (!isFinite(box)) continue;
            boolean pushable = currentlyPushable(entity);
            OecEntityAccess access = (OecEntityAccess) entity;
            access.rof$setPushableBit(pushable);

            int minX = cell(box.minX);
            int maxX = cell(box.maxX);
            int minY = cell(box.minY);
            int maxY = cell(box.maxY);
            int minZ = cell(box.minZ);
            int maxZ = cell(box.maxZ);

            int ownMinX = Math.max(minX, baseCellX);
            int ownMaxX = Math.min(maxX, baseCellX + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            int ownMinY = Math.max(minY, baseCellY);
            int ownMaxY = Math.min(maxY, baseCellY + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            int ownMinZ = Math.max(minZ, baseCellZ);
            int ownMaxZ = Math.min(maxZ, baseCellZ + SectionEntityGrid.SECTION_CELL_COUNT - 1);
            access.rof$setRegisteredCells(ownMinX <= ownMaxX && ownMinY <= ownMaxY && ownMinZ <= ownMaxZ
                    ? packRange(ownMinX & 7, ownMinY & 7, ownMinZ & 7, ownMaxX & 7, ownMaxY & 7, ownMaxZ & 7)
                    : EMPTY_RANGE);

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

    private static @Nullable OecSectionAccess neighbor(EntitySectionStorage<?> storage, int sectionX, int sectionY, int sectionZ) {
        long key = SectionPos.asLong(sectionX, sectionY, sectionZ);
        if (key != cachedNeighborKey) {
            EntitySection<?> section = storage.getSection(key);
            cachedNeighborKey = key;
            cachedNeighbor = section instanceof OecSectionAccess access ? access : null;
        }
        return cachedNeighbor;
    }
}
