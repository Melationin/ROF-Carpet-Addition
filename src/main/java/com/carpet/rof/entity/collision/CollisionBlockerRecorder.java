package com.carpet.rof.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;


public final class CollisionBlockerRecorder {
	public static final long UNSET = Long.MIN_VALUE;

	private static final long[] SLOTS = {UNSET, UNSET, UNSET}; // X、Y、Z
	private static boolean active;
	private static EntityCollisionCacheAccess target;

	private CollisionBlockerRecorder() {
	}

	public static void begin(EntityCollisionCacheAccess cache) {
		SLOTS[0] = UNSET;
		SLOTS[1] = UNSET;
		SLOTS[2] = UNSET;
		target = cache;
		active = true;
	}

	public static boolean isActive() {
		return active;
	}

	/* 返回值：本次记录所绑定的缓存；未开始记录时返回 null。 */
	public static EntityCollisionCacheAccess end() {
		if (!active) {
			return null;
		}
		final EntityCollisionCacheAccess cache = target;
		active = false;
		target = null;
		return cache;
	}

	public static void record(Direction.Axis axis, VoxelShape shape) {
		final AABB bounds = shape.bounds();
		// 实体和世界边界的形状会横跨多个格子，这里只对单个方块的形状有用。
		if (bounds.maxX - bounds.minX > 1.0 + 1.0E-7
				|| bounds.maxY - bounds.minY > 1.0 + 1.0E-7
				|| bounds.maxZ - bounds.minZ > 1.0 + 1.0E-7) {
			return;
		}
		SLOTS[axis.ordinal()] = BlockPos.asLong(
				Mth.floor(bounds.minX), Mth.floor(bounds.minY), Mth.floor(bounds.minZ));
	}

	public static void flush(EntityCollisionCacheAccess cache) {
		final long[] blockers = cache.rof$blockerSlots();
		for (int i = 0; i < SLOTS.length; i++) {
			if (SLOTS[i] != UNSET) {
				blockers[i] = SLOTS[i];
			}
		}
	}
}
