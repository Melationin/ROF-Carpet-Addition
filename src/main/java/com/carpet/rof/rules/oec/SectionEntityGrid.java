package com.carpet.rof.rules.oec;

import net.minecraft.world.entity.Entity;

import java.util.Arrays;

/** cell 直接持有实体条目与"可推"位；删除只置 null，不压缩。 */
public final class SectionEntityGrid {
    public static final int CELL_SIZE = 2;
    public static final int CELLS_PER_AXIS = 8;
    public static final int SECTION_CELL_COUNT = CELLS_PER_AXIS;
    private static final int CELL_COUNT = CELLS_PER_AXIS * CELLS_PER_AXIS * CELLS_PER_AXIS;
    private static final int INITIAL_CELL_CAPACITY = 4;

    private final boolean fine;
    private final int cellCount;
    private final Entity[][] cellEntities;
    private final long[][] cellMasks;
    private final int[] cellSizes;
    private boolean valid = true;

    public SectionEntityGrid(boolean fine) {
        this.fine = fine;
        this.cellCount = fine ? CELL_COUNT : 1;
        this.cellEntities = new Entity[this.cellCount][];
        this.cellMasks = new long[this.cellCount][];
        this.cellSizes = new int[this.cellCount];
    }

    public boolean isFine() { return this.fine; }
    public boolean isValid() { return this.valid; }

    public void clear() {
        for (int cell = 0; cell < this.cellCount; cell++) {
            int size = this.cellSizes[cell];
            if (size == 0) continue;
            Arrays.fill(this.cellEntities[cell], 0, size, null);
            Arrays.fill(this.cellMasks[cell], 0, ((size - 1) >>> 6) + 1, 0L);
            this.cellSizes[cell] = 0;
        }
    }

    public void add(int cell, Entity entity, boolean pushable) {
        Entity[] entities = this.cellEntities[cell];
        if (entities == null) {
            entities = this.cellEntities[cell] = new Entity[INITIAL_CELL_CAPACITY];
            this.cellMasks[cell] = new long[1];
        }
        int size = this.cellSizes[cell];
        if (size != 0 && entities[size - 1] == entity) return;
        if (size == entities.length) {
            entities = this.cellEntities[cell] = Arrays.copyOf(entities, size << 1);
            this.cellMasks[cell] = Arrays.copyOf(this.cellMasks[cell], ((entities.length - 1) >>> 6) + 1);
        }
        entities[size] = entity;
        if (pushable) this.cellMasks[cell][size >>> 6] |= 1L << (size & 63);
        this.cellSizes[cell] = size + 1;
    }

    /** 全局 cell 坐标 → 本网格内部的 cell 下标；粗网格整段只有一格。 */
    public int localCell(int cellX, int cellY, int cellZ) {
        if (!this.fine) return 0;
        return (cellX & 7) | ((cellZ & 7) << 3) | ((cellY & 7) << 6);
    }

    public Entity[] cellEntities(int cell) { return this.cellEntities[cell]; }
    public long[] cellMask(int cell) { return this.cellMasks[cell]; }
    public int cellSize(int cell) { return this.cellSizes[cell]; }

    public void release() {
        for (int cell = 0; cell < this.cellCount; cell++) {
            if (this.cellSizes[cell] != 0) Arrays.fill(this.cellEntities[cell], 0, this.cellSizes[cell], null);
            this.cellEntities[cell] = null;
            this.cellMasks[cell] = null;
            this.cellSizes[cell] = 0;
        }
        this.valid = false;
    }
}
