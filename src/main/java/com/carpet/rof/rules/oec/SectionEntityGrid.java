package com.carpet.rof.rules.oec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 单个 16x16x16 EntitySection 的实体空间索引。
 *
 * cellSize:
 *   16 -> 1x1x1，等价于无细分
 *    8 -> 2x2x2
 *    4 -> 4x4x4
 *    2 -> 8x8x8
 *    1 -> 16x16x16
 *
 * 无论是否细分：
 * - Entity[]
 * - position xyz
 * - AABB min/max
 *
 * 始终使用连续 dense storage。
 */
public final class SectionEntityGrid {

    private static final int SECTION_SIZE = 16;
    private static final int INITIAL_CAPACITY = 64;

    /* Section world-space origin */
    private final double originX;
    private final double originY;
    private final double originZ;

    /*
     * cellSize = 16 => 无细分
     * cellSize = 2  => 8^3 个 cell
     */
    private final int cellSize;
    private final int cellsPerAxis;
    private final int cellShift;

    /*
     * 每个 cell 是一个 dense-slot bitset。
     *
     * entity slot i 属于 cell c:
     * cells[c].get(i) == true
     */
    private final BitSet[] cells;

    /*
     * -------------------------
     * Dense entity storage
     * -------------------------
     */

    private Entity[] entities;

    private double[] posX;
    private double[] posY;
    private double[] posZ;

    private double[] minX;
    private double[] minY;
    private double[] minZ;

    private double[] maxX;
    private double[] maxY;
    private double[] maxZ;

    /*
     * 当前实体占据的 cell range。
     *
     * 这样 update/remove 时不用重新根据旧 AABB 计算。
     */
    private byte[] cellMinX;
    private byte[] cellMinY;
    private byte[] cellMinZ;

    private byte[] cellMaxX;
    private byte[] cellMaxY;
    private byte[] cellMaxZ;

    /*
     * Entity -> dense slot
     */
    private final IdentityHashMap<Entity, Integer> entityToIndex =
            new IdentityHashMap<>();

    private int size;

    /*
     * 查询 scratch。
     *
     * Minecraft server thread 单线程查询的话可以直接复用。
     * 如果未来并发，需要 ThreadLocal。
     */
    private final BitSet queryBits = new BitSet();


    public SectionEntityGrid(
            int sectionX,
            int sectionY,
            int sectionZ,
            int cellSize
    ) {
        if (cellSize <= 0
                || cellSize > SECTION_SIZE
                || (cellSize & (cellSize - 1)) != 0
                || SECTION_SIZE % cellSize != 0) {
            throw new IllegalArgumentException(
                    "cellSize must be one of 1, 2, 4, 8, 16"
            );
        }

        this.originX = sectionX * 16.0;
        this.originY = sectionY * 16.0;
        this.originZ = sectionZ * 16.0;

        this.cellSize = cellSize;
        this.cellsPerAxis = SECTION_SIZE / cellSize;

        /*
         * cellsPerAxis 一定为 2 的幂。
         */
        this.cellShift =
                Integer.numberOfTrailingZeros(cellsPerAxis);

        int cellCount =
                cellsPerAxis
                        * cellsPerAxis
                        * cellsPerAxis;

        this.cells = new BitSet[cellCount];

        for (int i = 0; i < cellCount; i++) {
            this.cells[i] = new BitSet();
        }

        allocateArrays(INITIAL_CAPACITY);
    }


    // ========================================================================
    // Add
    // ========================================================================

    public void add(Entity entity) {
        if (entityToIndex.containsKey(entity)) {
            return;
        }

        ensureCapacity(size + 1);

        int index = size++;

        entities[index] = entity;
        entityToIndex.put(entity, index);

        readEntity(index, entity);

        CellRange range = computeCellRange(
                minX[index],
                minY[index],
                minZ[index],
                maxX[index],
                maxY[index],
                maxZ[index]
        );

        storeRange(index, range);
        addToCells(index, range);
    }


    // ========================================================================
    // Remove
    // ========================================================================

    public void remove(Entity entity) {
        Integer boxedIndex = entityToIndex.remove(entity);

        if (boxedIndex == null) {
            return;
        }

        int index = boxedIndex;
        int last = size - 1;

        /*
         * 先移除被删除实体原来的 cell membership。
         */
        removeFromCells(index, rangeOf(index));

        if (index != last) {
            /*
             * last entity 将移动到 index。
             *
             * 先修改 grid：
             *
             * old:
             *     bit[last] = 1
             *
             * new:
             *     bit[index] = 1
             */
            CellRange movedRange = rangeOf(last);

            moveCellBits(last, index, movedRange);

            /*
             * 然后复制 dense data。
             */
            copySlot(last, index);

            Entity moved = entities[index];
            entityToIndex.put(moved, index);
        }

        clearSlot(last);

        size--;
    }


    // ========================================================================
    // Update
    // ========================================================================

    /**
     * 实体移动 / AABB 改变后调用。
     *
     * position 与 AABB 无论 cellSize 是否为 16 都始终更新。
     */
    public void update(Entity entity) {
        Integer boxedIndex = entityToIndex.get(entity);

        if (boxedIndex == null) {
            return;
        }

        int index = boxedIndex;

        CellRange oldRange = rangeOf(index);

        readEntity(index, entity);

        CellRange newRange = computeCellRange(
                minX[index],
                minY[index],
                minZ[index],
                maxX[index],
                maxY[index],
                maxZ[index]
        );

        if (!oldRange.equals(newRange)) {
            /*
             * 普通实体一般只跨很少几个 cell，
             * 所以简单 clear old + set new 即可。
             */
            removeFromCells(index, oldRange);
            addToCells(index, newRange);

            storeRange(index, newRange);
        }
    }


    // ========================================================================
    // Query
    // ========================================================================

    public void collect(
            AABB query,
            Predicate<? super Entity> predicate,
            Consumer<? super Entity> output
    ) {
        CellRange range = computeCellRange(
                query.minX,
                query.minY,
                query.minZ,
                query.maxX,
                query.maxY,
                query.maxZ
        );

        if (range.empty()) {
            return;
        }

        /*
         * cellSize == 16 时：
         *
         * cells.length == 1
         *
         * 所以这里自然退化为扫描所有实体，
         * 不需要任何特殊分支。
         */
        queryBits.clear();

        for (int y = range.minY; y <= range.maxY; y++) {
            for (int z = range.minZ; z <= range.maxZ; z++) {

                int base = cellIndex(
                        range.minX,
                        y,
                        z
                );

                for (int x = range.minX; x <= range.maxX; x++) {
                    queryBits.or(
                            cells[base + x - range.minX]
                    );
                }
            }
        }

        /*
         * 多 cell 实体会自动被 BitSet 去重。
         */
        for (
                int i = queryBits.nextSetBit(0);
                i >= 0;
                i = queryBits.nextSetBit(i + 1)
        ) {
            /*
             * 先用 dense AABB，
             * 不访问 Entity#getBoundingBox()。
             */
            if (!intersects(i, query)) {
                continue;
            }

            Entity entity = entities[i];

            if (!predicate.test(entity)) {
                continue;
            }

            output.accept(entity);
        }
    }


    /**
     * 不需要 predicate 的版本。
     */
    public void collect(
            AABB query,
            Consumer<? super Entity> output
    ) {
        collect(query, e -> true, output);
    }


    // ========================================================================
    // Dense data
    // ========================================================================

    private void readEntity(int i, Entity entity) {
        posX[i] = entity.getX();
        posY[i] = entity.getY();
        posZ[i] = entity.getZ();

        AABB box = entity.getBoundingBox();

        minX[i] = box.minX;
        minY[i] = box.minY;
        minZ[i] = box.minZ;

        maxX[i] = box.maxX;
        maxY[i] = box.maxY;
        maxZ[i] = box.maxZ;
    }


    private boolean intersects(int i, AABB box) {
        /*
         * 等价于 AABB.intersects，
         * 但直接读连续 primitive array。
         */
        return maxX[i] > box.minX
                && minX[i] < box.maxX
                && maxY[i] > box.minY
                && minY[i] < box.maxY
                && maxZ[i] > box.minZ
                && minZ[i] < box.maxZ;
    }


    // ========================================================================
    // Cell range
    // ========================================================================

    private CellRange computeCellRange(
            double boxMinX,
            double boxMinY,
            double boxMinZ,
            double boxMaxX,
            double boxMaxY,
            double boxMaxZ
    ) {
        /*
         * 完全不与这个 EntitySection 相交。
         */
        if (boxMaxX <= originX
                || boxMinX >= originX + 16.0
                || boxMaxY <= originY
                || boxMinY >= originY + 16.0
                || boxMaxZ <= originZ
                || boxMinZ >= originZ + 16.0) {

            return CellRange.EMPTY;
        }

        int minCX = toCellMin(boxMinX, originX);
        int minCY = toCellMin(boxMinY, originY);
        int minCZ = toCellMin(boxMinZ, originZ);

        int maxCX = toCellMax(boxMaxX, originX);
        int maxCY = toCellMax(boxMaxY, originY);
        int maxCZ = toCellMax(boxMaxZ, originZ);

        return new CellRange(
                minCX,
                minCY,
                minCZ,
                maxCX,
                maxCY,
                maxCZ
        );
    }


    private int toCellMin(double p, double origin) {
        int cell = (int) Math.floor(
                (p - origin) / cellSize
        );

        return clampCell(cell);
    }


    private int toCellMax(double p, double origin) {
        /*
         * max 边界视为 exclusive。
         */
        int cell = (int) Math.floor(
                (Math.nextDown(p) - origin) / cellSize
        );

        return clampCell(cell);
    }


    private int clampCell(int cell) {
        if (cell < 0) {
            return 0;
        }

        if (cell >= cellsPerAxis) {
            return cellsPerAxis - 1;
        }

        return cell;
    }


    private int cellIndex(int x, int y, int z) {
        /*
         * X contiguous:
         *
         * index =
         *     x
         *   + z * N
         *   + y * N*N
         */
        return x
                | (z << cellShift)
                | (y << (cellShift << 1));
    }


    // ========================================================================
    // Cell membership
    // ========================================================================

    private void addToCells(int entityIndex, CellRange range) {
        if (range.empty()) {
            return;
        }

        for (int y = range.minY; y <= range.maxY; y++) {
            for (int z = range.minZ; z <= range.maxZ; z++) {
                int base = cellIndex(
                        range.minX,
                        y,
                        z
                );

                for (int x = range.minX; x <= range.maxX; x++) {
                    cells[base + x - range.minX]
                            .set(entityIndex);
                }
            }
        }
    }


    private void removeFromCells(int entityIndex, CellRange range) {
        if (range.empty()) {
            return;
        }

        for (int y = range.minY; y <= range.maxY; y++) {
            for (int z = range.minZ; z <= range.maxZ; z++) {
                int base = cellIndex(
                        range.minX,
                        y,
                        z
                );

                for (int x = range.minX; x <= range.maxX; x++) {
                    cells[base + x - range.minX]
                            .clear(entityIndex);
                }
            }
        }
    }


    private void moveCellBits(
            int oldIndex,
            int newIndex,
            CellRange range
    ) {
        if (range.empty()) {
            return;
        }

        for (int y = range.minY; y <= range.maxY; y++) {
            for (int z = range.minZ; z <= range.maxZ; z++) {
                int base = cellIndex(
                        range.minX,
                        y,
                        z
                );

                for (int x = range.minX; x <= range.maxX; x++) {
                    BitSet bits =
                            cells[base + x - range.minX];

                    bits.clear(oldIndex);
                    bits.set(newIndex);
                }
            }
        }
    }


    // ========================================================================
    // Stored cell range
    // ========================================================================

    private CellRange rangeOf(int i) {
        return new CellRange(
                Byte.toUnsignedInt(cellMinX[i]),
                Byte.toUnsignedInt(cellMinY[i]),
                Byte.toUnsignedInt(cellMinZ[i]),

                Byte.toUnsignedInt(cellMaxX[i]),
                Byte.toUnsignedInt(cellMaxY[i]),
                Byte.toUnsignedInt(cellMaxZ[i])
        );
    }


    private void storeRange(int i, CellRange range) {
        if (range.empty()) {
            /*
             * 如果你保证加入这个 index 的实体一定与 section 相交，
             * 这里理论上不会发生。
             */
            cellMinX[i] = 0;
            cellMinY[i] = 0;
            cellMinZ[i] = 0;

            cellMaxX[i] = 0;
            cellMaxY[i] = 0;
            cellMaxZ[i] = 0;
            return;
        }

        cellMinX[i] = (byte) range.minX;
        cellMinY[i] = (byte) range.minY;
        cellMinZ[i] = (byte) range.minZ;

        cellMaxX[i] = (byte) range.maxX;
        cellMaxY[i] = (byte) range.maxY;
        cellMaxZ[i] = (byte) range.maxZ;
    }


    // ========================================================================
    // Dense remove
    // ========================================================================

    private void copySlot(int src, int dst) {
        entities[dst] = entities[src];

        posX[dst] = posX[src];
        posY[dst] = posY[src];
        posZ[dst] = posZ[src];

        minX[dst] = minX[src];
        minY[dst] = minY[src];
        minZ[dst] = minZ[src];

        maxX[dst] = maxX[src];
        maxY[dst] = maxY[src];
        maxZ[dst] = maxZ[src];

        cellMinX[dst] = cellMinX[src];
        cellMinY[dst] = cellMinY[src];
        cellMinZ[dst] = cellMinZ[src];

        cellMaxX[dst] = cellMaxX[src];
        cellMaxY[dst] = cellMaxY[src];
        cellMaxZ[dst] = cellMaxZ[src];
    }


    private void clearSlot(int i) {
        entities[i] = null;
    }


    // ========================================================================
    // Capacity
    // ========================================================================

    private void ensureCapacity(int required) {
        if (required <= entities.length) {
            return;
        }

        int capacity = entities.length;

        while (capacity < required) {
            capacity <<= 1;
        }

        entities = Arrays.copyOf(entities, capacity);

        posX = Arrays.copyOf(posX, capacity);
        posY = Arrays.copyOf(posY, capacity);
        posZ = Arrays.copyOf(posZ, capacity);

        minX = Arrays.copyOf(minX, capacity);
        minY = Arrays.copyOf(minY, capacity);
        minZ = Arrays.copyOf(minZ, capacity);

        maxX = Arrays.copyOf(maxX, capacity);
        maxY = Arrays.copyOf(maxY, capacity);
        maxZ = Arrays.copyOf(maxZ, capacity);

        cellMinX = Arrays.copyOf(cellMinX, capacity);
        cellMinY = Arrays.copyOf(cellMinY, capacity);
        cellMinZ = Arrays.copyOf(cellMinZ, capacity);

        cellMaxX = Arrays.copyOf(cellMaxX, capacity);
        cellMaxY = Arrays.copyOf(cellMaxY, capacity);
        cellMaxZ = Arrays.copyOf(cellMaxZ, capacity);
    }


    private void allocateArrays(int capacity) {
        entities = new Entity[capacity];

        posX = new double[capacity];
        posY = new double[capacity];
        posZ = new double[capacity];

        minX = new double[capacity];
        minY = new double[capacity];
        minZ = new double[capacity];

        maxX = new double[capacity];
        maxY = new double[capacity];
        maxZ = new double[capacity];

        cellMinX = new byte[capacity];
        cellMinY = new byte[capacity];
        cellMinZ = new byte[capacity];

        cellMaxX = new byte[capacity];
        cellMaxY = new byte[capacity];
        cellMaxZ = new byte[capacity];
    }


    // ========================================================================
    // Accessors
    // ========================================================================

    public int size() {
        return size;
    }

    public int cellSize() {
        return cellSize;
    }

    public int cellsPerAxis() {
        return cellsPerAxis;
    }

    public Entity entity(int index) {
        return entities[index];
    }

    public double x(int index) {
        return posX[index];
    }

    public double y(int index) {
        return posY[index];
    }

    public double z(int index) {
        return posZ[index];
    }


    // ========================================================================

    private record CellRange(
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {
        static final CellRange EMPTY =
                new CellRange(0, 0, 0, -1, -1, -1);

        boolean empty() {
            return maxX < minX
                    || maxY < minY
                    || maxZ < minZ;
        }
    }
}