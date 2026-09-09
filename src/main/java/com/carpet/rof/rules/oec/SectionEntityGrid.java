package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.BitSet;

/** A dense, section-local broad-phase index for Lithium push queries. */
public final class SectionEntityGrid {
    public static final int CELL_SIZE = 2;
    private static final int CELLS_PER_AXIS = 8;
    private static final int CELL_COUNT = 512;
    private static final int INITIAL_CAPACITY = 16;

    private final double originX;
    private final double originY;
    private final double originZ;
    private final Reference2IntOpenHashMap<Entity> entityToSlot = new Reference2IntOpenHashMap<>();

    private Entity[] entities = new Entity[INITIAL_CAPACITY];
    private double[] minX = new double[INITIAL_CAPACITY];
    private double[] minY = new double[INITIAL_CAPACITY];
    private double[] minZ = new double[INITIAL_CAPACITY];
    private double[] maxX = new double[INITIAL_CAPACITY];
    private double[] maxY = new double[INITIAL_CAPACITY];
    private double[] maxZ = new double[INITIAL_CAPACITY];
    private byte[] cellMinX = new byte[INITIAL_CAPACITY];
    private byte[] cellMinY = new byte[INITIAL_CAPACITY];
    private byte[] cellMinZ = new byte[INITIAL_CAPACITY];
    private byte[] cellMaxX = new byte[INITIAL_CAPACITY];
    private byte[] cellMaxY = new byte[INITIAL_CAPACITY];
    private byte[] cellMaxZ = new byte[INITIAL_CAPACITY];
    private BitSet[] cells;
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
        CellRange range = computeCellRange(box);
        storeRange(slot, range);
        if (this.cells != null) addToCells(slot, range);
        this.modificationCount++;
        return true;
    }

    public boolean remove(Entity entity) {
        int slot = this.entityToSlot.removeInt(entity);
        if (slot < 0) return false;
        int last = this.size - 1;
        if (this.cells != null) removeFromCells(slot, rangeOf(slot));
        if (slot != last) {
            CellRange movedRange = rangeOf(last);
            if (this.cells != null) removeFromCells(last, movedRange);
            copySlot(last, slot);
            this.entityToSlot.put(this.entities[slot], slot);
            if (this.cells != null) addToCells(slot, movedRange);
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
        OecMetrics.BOUNDS_UPDATES.increment();
        CellRange oldRange = rangeOf(slot);
        writeBounds(slot, box);
        CellRange newRange = computeCellRange(box);
        if (this.cells != null && !oldRange.equals(newRange)) {
            OecMetrics.RANGE_CHANGES.increment();
            removeFromCells(slot, oldRange);
            addToCells(slot, newRange);
        }
        storeRange(slot, newRange);
        this.modificationCount++;
    }

    public void enableFineGrid() {
        if (this.cells != null || !this.valid) return;
        this.cells = new BitSet[CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) this.cells[i] = new BitSet(this.entities.length);
        for (int slot = 0; slot < this.size; slot++) addToCells(slot, rangeOf(slot));
    }

    /** Populates frame with broad-phase slots. Returns false when Lithium must be used. */
    public boolean collectCandidateSlots(AABB box, OecQueryFrame frame) {
        if (!this.valid || !isFinite(box)) return false;
        BitSet candidates = frame.bits();
        candidates.clear();
        if (this.cells == null) {
            candidates.set(0, this.size);
            return true;
        }
        CellRange range = computeCellRange(box);
        for (int y = range.minY; y <= range.maxY; y++) {
            for (int z = range.minZ; z <= range.maxZ; z++) {
                int base = cellIndex(range.minX, y, z);
                for (int x = range.minX; x <= range.maxX; x++) candidates.or(this.cells[base + x - range.minX]);
            }
        }
        return true;
    }

    public boolean intersects(int slot, AABB box) {
        return slot >= 0 && slot < this.size
                && this.maxX[slot] > box.minX && this.minX[slot] < box.maxX
                && this.maxY[slot] > box.minY && this.minY[slot] < box.maxY
                && this.maxZ[slot] > box.minZ && this.minZ[slot] < box.maxZ;
    }

    public Entity entity(int slot) { return slot >= 0 && slot < this.size ? this.entities[slot] : null; }
    public int size() { return this.size; }
    public boolean isValid() { return this.valid; }
    public long modificationCount() { return this.modificationCount; }

    public void release() {
        Arrays.fill(this.entities, 0, this.size, null);
        this.entityToSlot.clear();
        this.cells = null;
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
            if (this.cells != null) {
                CellRange range = rangeOf(slot);
                for (int y = 0; y < CELLS_PER_AXIS; y++) for (int z = 0; z < CELLS_PER_AXIS; z++) for (int x = 0; x < CELLS_PER_AXIS; x++) {
                    boolean expected = x >= range.minX && x <= range.maxX && y >= range.minY && y <= range.maxY && z >= range.minZ && z <= range.maxZ;
                    if (this.cells[cellIndex(x, y, z)].get(slot) != expected) throw new IllegalStateException("Invalid membership for slot " + slot);
                }
            }
        }
    }

    private boolean invalidate() { this.valid = false; this.modificationCount++; return false; }
    private void writeBounds(int slot, AABB box) {
        this.minX[slot] = box.minX; this.minY[slot] = box.minY; this.minZ[slot] = box.minZ;
        this.maxX[slot] = box.maxX; this.maxY[slot] = box.maxY; this.maxZ[slot] = box.maxZ;
    }
    private boolean sameBounds(int slot, AABB box) {
        return this.minX[slot] == box.minX && this.minY[slot] == box.minY && this.minZ[slot] == box.minZ
                && this.maxX[slot] == box.maxX && this.maxY[slot] == box.maxY && this.maxZ[slot] == box.maxZ;
    }
    private static boolean isFinite(AABB box) {
        return Double.isFinite(box.minX) && Double.isFinite(box.minY) && Double.isFinite(box.minZ)
                && Double.isFinite(box.maxX) && Double.isFinite(box.maxY) && Double.isFinite(box.maxZ);
    }
    private CellRange computeCellRange(AABB box) {
        return new CellRange(toCell(box.minX, this.originX), toCell(box.minY, this.originY), toCell(box.minZ, this.originZ),
                toCell(box.maxX, this.originX), toCell(box.maxY, this.originY), toCell(box.maxZ, this.originZ));
    }
    private static int toCell(double coordinate, double origin) {
        return Math.max(0, Math.min(CELLS_PER_AXIS - 1, (int) Math.floor((coordinate - origin) / CELL_SIZE)));
    }
    private static int cellIndex(int x, int y, int z) { return x | (z << 3) | (y << 6); }
    private void addToCells(int slot, CellRange range) {
        for (int y = range.minY; y <= range.maxY; y++) for (int z = range.minZ; z <= range.maxZ; z++) {
            int base = cellIndex(range.minX, y, z);
            for (int x = range.minX; x <= range.maxX; x++) this.cells[base + x - range.minX].set(slot);
        }
    }
    private void removeFromCells(int slot, CellRange range) {
        for (int y = range.minY; y <= range.maxY; y++) for (int z = range.minZ; z <= range.maxZ; z++) {
            int base = cellIndex(range.minX, y, z);
            for (int x = range.minX; x <= range.maxX; x++) this.cells[base + x - range.minX].clear(slot);
        }
    }
    private CellRange rangeOf(int slot) {
        return new CellRange(Byte.toUnsignedInt(this.cellMinX[slot]), Byte.toUnsignedInt(this.cellMinY[slot]), Byte.toUnsignedInt(this.cellMinZ[slot]),
                Byte.toUnsignedInt(this.cellMaxX[slot]), Byte.toUnsignedInt(this.cellMaxY[slot]), Byte.toUnsignedInt(this.cellMaxZ[slot]));
    }
    private void storeRange(int slot, CellRange range) {
        this.cellMinX[slot] = (byte) range.minX; this.cellMinY[slot] = (byte) range.minY; this.cellMinZ[slot] = (byte) range.minZ;
        this.cellMaxX[slot] = (byte) range.maxX; this.cellMaxY[slot] = (byte) range.maxY; this.cellMaxZ[slot] = (byte) range.maxZ;
    }
    private void copySlot(int source, int target) {
        this.entities[target] = this.entities[source];
        this.minX[target] = this.minX[source]; this.minY[target] = this.minY[source]; this.minZ[target] = this.minZ[source];
        this.maxX[target] = this.maxX[source]; this.maxY[target] = this.maxY[source]; this.maxZ[target] = this.maxZ[source];
        this.cellMinX[target] = this.cellMinX[source]; this.cellMinY[target] = this.cellMinY[source]; this.cellMinZ[target] = this.cellMinZ[source];
        this.cellMaxX[target] = this.cellMaxX[source]; this.cellMaxY[target] = this.cellMaxY[source]; this.cellMaxZ[target] = this.cellMaxZ[source];
    }
    private void ensureCapacity(int required) {
        if (required <= this.entities.length) return;
        int capacity = this.entities.length;
        while (capacity < required) capacity <<= 1;
        this.entities = Arrays.copyOf(this.entities, capacity);
        this.minX = Arrays.copyOf(this.minX, capacity); this.minY = Arrays.copyOf(this.minY, capacity); this.minZ = Arrays.copyOf(this.minZ, capacity);
        this.maxX = Arrays.copyOf(this.maxX, capacity); this.maxY = Arrays.copyOf(this.maxY, capacity); this.maxZ = Arrays.copyOf(this.maxZ, capacity);
        this.cellMinX = Arrays.copyOf(this.cellMinX, capacity); this.cellMinY = Arrays.copyOf(this.cellMinY, capacity); this.cellMinZ = Arrays.copyOf(this.cellMinZ, capacity);
        this.cellMaxX = Arrays.copyOf(this.cellMaxX, capacity); this.cellMaxY = Arrays.copyOf(this.cellMaxY, capacity); this.cellMaxZ = Arrays.copyOf(this.cellMaxZ, capacity);
    }
    private record CellRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {}
}
