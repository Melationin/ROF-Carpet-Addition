package com.carpet.rof.rules.oec;

import net.minecraft.world.phys.AABB;

/** 全局 cell 坐标换算与"已登记格范围"的打包。cell 边长固定为 2 格方块。 */
public final class OecCells {
    public static final int EMPTY_RANGE = -1;
    private static final int RANGE_MASK = 7;

    private OecCells() {}

    public static int cell(double coordinate) {
        return (int) Math.floor(coordinate * 0.5);
    }

    public static int sectionOfCell(int cell) { return cell >> 3; }

    public static int localOfCell(int cell) { return cell & RANGE_MASK; }

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
}
