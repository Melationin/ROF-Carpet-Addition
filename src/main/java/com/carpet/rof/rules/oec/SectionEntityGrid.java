package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;

/** A dense, section-local broad-phase index for Lithium push queries. */
public final class SectionEntityGrid {
    public static final int CELL_SIZE = 2;
    private static final int CELLS_PER_AXIS = 8;
    private static final int CELL_COUNT = 512;
    private static final int INITIAL_CAPACITY = 16;
    private static final int STRIDE = 6;
    private static final int MIN_X = 0;
    private static final int MIN_Y = 1;
    private static final int MIN_Z = 2;
    private static final int MAX_X = 3;
    private static final int MAX_Y = 4;
    private static final int MAX_Z = 5;
    private static final int RANGE_MASK = 7;

    private final double originX;
    private final double originY;
    private final double originZ;
    private final Reference2IntOpenHashMap<Entity> entityToSlot = new Reference2IntOpenHashMap<>();

    private Entity[] entities = new Entity[INITIAL_CAPACITY];
    // 每个槽位依次存储 minX、minY、minZ、maxX、maxY、maxZ。
    private double[] bounds = new double[INITIAL_CAPACITY * STRIDE];
    // 坐标按无符号字节存储，顺序与 bounds 相同。
    private byte[] cellRanges = new byte[INITIAL_CAPACITY * STRIDE];
    // 每个 cell 占用 wordsPerCell 个 long。
    private long[] cellBits;
    private int wordsPerCell;
    private int size;
    private boolean valid = true;
    private long modificationCount;

    public SectionEntityGrid(int sectionX, int sectionY, int sectionZ) {
        this.originX = sectionX * 16.0;
        this.originY = sectionY * 16.0;
        this.originZ = sectionZ * 16.0;
        this.entityToSlot.defaultReturnValue(-1);
    }

    public boolean add(Entity entity) {
        if (!this.valid) return false;
        if (this.entityToSlot.getInt(entity) >= 0) return true;
        AABB box = entity.getBoundingBox();
        if (!isFinite(box)) return invalidate();
        ensureCapacity(this.size + 1);
        int slot = this.size++;
        this.entities[slot] = entity;
        this.entityToSlot.put(entity, slot);
        writeBounds(slot, box);
        int range = computeCellRange(box);
        storeRange(slot, range);
        if (this.cellBits != null) addToCells(slot, range);
        this.modificationCount++;
        return true;
    }

    public boolean remove(Entity entity) {
        int slot = this.entityToSlot.removeInt(entity);
        if (slot < 0) return false;
        int last = this.size - 1;
        if (this.cellBits != null) removeFromCells(slot, rangeOf(slot));
        if (slot != last) {
            int movedRange = rangeOf(last);
            if (this.cellBits != null) removeFromCells(last, movedRange);
            copySlot(last, slot);
            this.entityToSlot.put(this.entities[slot], slot);
            if (this.cellBits != null) addToCells(slot, movedRange);
        }
        this.entities[last] = null;
        this.size--;
        this.modificationCount++;
        return true;
    }

    public void updateBounds(Entity entity, AABB box) {
        int slot = this.entityToSlot.getInt(entity);
        if (slot < 0 || !this.valid) return;
        if (!isFinite(box)) {
            invalidate();
            return;
        }
        if (sameBounds(slot, box)) return;
        if (OecMetrics.ENABLED) OecMetrics.BOUNDS_UPDATES.increment();
        int oldRange = rangeOf(slot);
        writeBounds(slot, box);
        int newRange = computeCellRange(box);
        if (this.cellBits != null && oldRange != newRange) {
            if (OecMetrics.ENABLED) OecMetrics.RANGE_CHANGES.increment();
            removeFromCells(slot, oldRange);
            addToCells(slot, newRange);
        }
        storeRange(slot, newRange);
        this.modificationCount++;
    }

    public void enableFineGrid() {
        if (this.cellBits != null || !this.valid) return;
        this.wordsPerCell = wordsFor(this.entities.length);
        this.cellBits = new long[CELL_COUNT * this.wordsPerCell];
        for (int slot = 0; slot < this.size; slot++) addToCells(slot, rangeOf(slot));
    }

    // 返回 false 时调用方必须回退 Lithium。
    public boolean collectCandidateSlots(AABB box, OecQueryFrame frame) {
        if (!this.valid || !isFinite(box)) return false;
        if (this.cellBits == null) {
            frame.fillAll(this.size);
            return true;
        }
        long[] bits = this.cellBits;
        int words = this.wordsPerCell;
        long[] union = frame.resetWords(words);
        int range = computeCellRange(box);
        int minX = rangeMinX(range), maxX = rangeMaxX(range);
        int minZ = rangeMinZ(range), maxZ = rangeMaxZ(range);
        for (int y = rangeMinY(range); y <= rangeMaxY(range); y++) {
            for (int z = minZ; z <= maxZ; z++) {
                int cellBase = cellIndex(minX, y, z) * words;
                for (int x = minX; x <= maxX; x++) {
                    int base = cellBase + (x - minX) * words;
                    for (int w = 0; w < words; w++) union[w] |= bits[base + w];
                }
            }
        }
        return true;
    }

    public boolean intersects(int slot, AABB box) {
        if (slot < 0 || slot >= this.size) return false;
        int base = slot * STRIDE;
        return this.bounds[base + MAX_X] > box.minX && this.bounds[base + MIN_X] < box.maxX
                && this.bounds[base + MAX_Y] > box.minY && this.bounds[base + MIN_Y] < box.maxY
                && this.bounds[base + MAX_Z] > box.minZ && this.bounds[base + MIN_Z] < box.maxZ;
    }

    public Entity entity(int slot) { return slot >= 0 && slot < this.size ? this.entities[slot] : null; }
    public int size() { return this.size; }
    public boolean isValid() { return this.valid; }
    public long modificationCount() { return this.modificationCount; }

    public void release() {
        Arrays.fill(this.entities, 0, this.size, null);
        this.entityToSlot.clear();
        this.cellBits = null;
        this.wordsPerCell = 0;
        this.size = 0;
        this.valid = false;
        this.modificationCount++;
    }

    public void checkInvariants() {
        if (!this.valid) throw new IllegalStateException("Entity grid is invalid");
        if (this.entityToSlot.size() != this.size) throw new IllegalStateException("Entity map size mismatch");
        for (int slot = 0; slot < this.size; slot++) {
            Entity entity = this.entities[slot];
            if (entity == null || this.entityToSlot.getInt(entity) != slot) throw new IllegalStateException("Invalid dense slot " + slot);
            if (this.cellBits != null) {
                int range = rangeOf(slot);
                int minX = rangeMinX(range), maxX = rangeMaxX(range),
                    minY = rangeMinY(range), maxY = rangeMaxY(range),
                    minZ = rangeMinZ(range), maxZ = rangeMaxZ(range);
                for (int y = 0; y < CELLS_PER_AXIS; y++) for (int z = 0; z < CELLS_PER_AXIS; z++) for (int x = 0; x < CELLS_PER_AXIS; x++) {
                    boolean expected = x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
                    if (getCellBit(cellIndex(x, y, z), slot) != expected) throw new IllegalStateException("Invalid membership for slot " + slot);
                }
            }
        }
    }

    private boolean invalidate() { this.valid = false; this.modificationCount++; return false; }
    private void writeBounds(int slot, AABB box) {
        int base = slot * STRIDE;
        this.bounds[base + MIN_X] = box.minX;
        this.bounds[base + MIN_Y] = box.minY;
        this.bounds[base + MIN_Z] = box.minZ;
        this.bounds[base + MAX_X] = box.maxX;
        this.bounds[base + MAX_Y] = box.maxY;
        this.bounds[base + MAX_Z] = box.maxZ;
    }

    private boolean sameBounds(int slot, AABB box) {
        int base = slot * STRIDE;
        return this.bounds[base + MIN_X] == box.minX && this.bounds[base + MIN_Y] == box.minY && this.bounds[base + MIN_Z] == box.minZ
                && this.bounds[base + MAX_X] == box.maxX && this.bounds[base + MAX_Y] == box.maxY && this.bounds[base + MAX_Z] == box.maxZ;
    }

    private static boolean isFinite(AABB box) {
        return Double.isFinite(box.minX) && Double.isFinite(box.minY) && Double.isFinite(box.minZ)
                && Double.isFinite(box.maxX) && Double.isFinite(box.maxY) && Double.isFinite(box.maxZ);
    }

    private int computeCellRange(AABB box) {
        return packRange(toCell(box.minX, this.originX), toCell(box.minY, this.originY), toCell(box.minZ, this.originZ),
                toCell(box.maxX, this.originX), toCell(box.maxY, this.originY), toCell(box.maxZ, this.originZ));
    }

    // 六个 0..7 的坐标各占 3 bit。
    private static int packRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return minX | (minY << 3) | (minZ << 6) | (maxX << 9) | (maxY << 12) | (maxZ << 15);
    }

    private static int rangeMinX(int range) {
        return range & RANGE_MASK;
    }

    private static int rangeMinY(int range) {
        return (range >>> 3) & RANGE_MASK;
    }

    private static int rangeMinZ(int range) {
        return (range >>> 6) & RANGE_MASK;
    }

    private static int rangeMaxX(int range) {
        return (range >>> 9) & RANGE_MASK;
    }

    private static int rangeMaxY(int range) {
        return (range >>> 12) & RANGE_MASK;
    }

    private static int rangeMaxZ(int range) {
        return (range >>> 15) & RANGE_MASK;
    }

    private static int toCell(double coordinate, double origin) {
        return Math.max(0, Math.min(CELLS_PER_AXIS - 1, (int) Math.floor((coordinate - origin) / CELL_SIZE)));
    }

    private static int cellIndex(int x, int y, int z) { return x | (z << 3) | (y << 6); }
    private void addToCells(int slot, int range) {
        int minX = rangeMinX(range), maxX = rangeMaxX(range);
        int minZ = rangeMinZ(range), maxZ = rangeMaxZ(range);
        for (int y = rangeMinY(range); y <= rangeMaxY(range); y++) for (int z = minZ; z <= maxZ; z++) {
            int base = cellIndex(minX, y, z);
            for (int x = minX; x <= maxX; x++) setCellBit(base + x - minX, slot);
        }
    }

    private void removeFromCells(int slot, int range) {
        int minX = rangeMinX(range), maxX = rangeMaxX(range);
        int minZ = rangeMinZ(range), maxZ = rangeMaxZ(range);
        for (int y = rangeMinY(range); y <= rangeMaxY(range); y++) for (int z = minZ; z <= maxZ; z++) {
            int base = cellIndex(minX, y, z);
            for (int x = minX; x <= maxX; x++) clearCellBit(base + x - minX, slot);
        }
    }

    private void setCellBit(int cell, int slot) {
        this.cellBits[cell * this.wordsPerCell + (slot >>> 6)] |= 1L << (slot & 63);
    }

    private void clearCellBit(int cell, int slot) {
        this.cellBits[cell * this.wordsPerCell + (slot >>> 6)] &= ~(1L << (slot & 63));
    }

    private boolean getCellBit(int cell, int slot) {
        return (this.cellBits[cell * this.wordsPerCell + (slot >>> 6)] & (1L << (slot & 63))) != 0L;
    }

    private static int wordsFor(int capacity) {
        return (capacity + 63) >>> 6;
    }

    private int rangeOf(int slot) {
        int base = slot * STRIDE;
        return packRange(Byte.toUnsignedInt(this.cellRanges[base + MIN_X]), Byte.toUnsignedInt(this.cellRanges[base + MIN_Y]), Byte.toUnsignedInt(this.cellRanges[base + MIN_Z]),
                Byte.toUnsignedInt(this.cellRanges[base + MAX_X]), Byte.toUnsignedInt(this.cellRanges[base + MAX_Y]), Byte.toUnsignedInt(this.cellRanges[base + MAX_Z]));
    }

    private void storeRange(int slot, int range) {
        int base = slot * STRIDE;
        this.cellRanges[base + MIN_X] = (byte) rangeMinX(range);
        this.cellRanges[base + MIN_Y] = (byte) rangeMinY(range);
        this.cellRanges[base + MIN_Z] = (byte) rangeMinZ(range);
        this.cellRanges[base + MAX_X] = (byte) rangeMaxX(range);
        this.cellRanges[base + MAX_Y] = (byte) rangeMaxY(range);
        this.cellRanges[base + MAX_Z] = (byte) rangeMaxZ(range);
    }

    private void copySlot(int source, int target) {
        this.entities[target] = this.entities[source];
        int sourceBase = source * STRIDE;
        int targetBase = target * STRIDE;
        System.arraycopy(this.bounds, sourceBase, this.bounds, targetBase, STRIDE);
        System.arraycopy(this.cellRanges, sourceBase, this.cellRanges, targetBase, STRIDE);
    }

    private void ensureCapacity(int required) {
        if (required <= this.entities.length) return;
        int capacity = this.entities.length;
        while (capacity < required) capacity <<= 1;
        this.entities = Arrays.copyOf(this.entities, capacity);
        this.bounds = Arrays.copyOf(this.bounds, capacity * STRIDE);
        this.cellRanges = Arrays.copyOf(this.cellRanges, capacity * STRIDE);
        if (this.cellBits != null) growCellBits(capacity);
    }

    private void growCellBits(int capacity) {
        int newWords = wordsFor(capacity);
        if (newWords == this.wordsPerCell) return;
        long[] grown = new long[CELL_COUNT * newWords];
        for (int cell = 0; cell < CELL_COUNT; cell++) {
            System.arraycopy(this.cellBits, cell * this.wordsPerCell, grown, cell * newWords, this.wordsPerCell);
        }
        this.cellBits = grown;
        this.wordsPerCell = newWords;
    }
}
