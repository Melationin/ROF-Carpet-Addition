package com.carpet.rof.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/*
 * Entity.collide(Vec3) 的跨 tick 缓存优化：对所有有位移的轴，用上一 tick 夹住它们的方块做探测。
 * 每次探测都会实时复核；如果所有轴仍然精确夹在 0.0，就跳过整次扫描并直接返回 Vec3.ZERO。
 * 否则返回 null，交给原版 / Lithium 实现，并让 CollisionBlockerRecorder 从这次扫描中记录新的阻挡方块。
 */
public final class EntityCollisionHelper {
	private static final double EPSILON = 1.0E-7;
	private static final double UNKNOWN = Double.NEGATIVE_INFINITY;

	private static final int AXIS_X = 0;
	private static final int AXIS_Y = 1;
	private static final int AXIS_Z = 2;
	private static final Direction.Axis[] AXES = {
			Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z
	};

	private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

	private EntityCollisionHelper() {
	}

	/* 复用的线程本地读取状态。 */
	private static final class Scratch {
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		final CollisionContext[] ctxHolder = new CollisionContext[1];
		final double[] clampTop = {UNKNOWN, UNKNOWN, UNKNOWN};
		boolean inUse;
	}

	/* 返回值：当所有有位移的轴都被证明夹住时返回 Vec3.ZERO，需要走原版实现时返回 null。 */
	public static Vec3 collide(
			Entity entity,
			EntityCollisionCacheAccess cache,
			Level level,
			AABB box,
			Vec3 movement,
			float maxUpStep,
			boolean onGround
	) {
		final double dx = movement.x;
		final double dy = movement.y;
		final double dz = movement.z;
		if (movement.lengthSqr() == 0.0) {
			return movement;
		}

		Scratch s = SCRATCH.get();
		if (s.inUse) {
			s = new Scratch();
		} else {
			s.inUse = true;
		}
		try {
			final long[] blockers = cache.rof$blockerSlots();
			if (dy != 0.0 && !probeBlockClamp(s, level, entity, blockers[AXIS_Y], AXIS_Y, dy, box)) {
				return fail();
			}
			if (dx != 0.0 && !probeBlockClamp(s, level, entity, blockers[AXIS_X], AXIS_X, dx, box)) {
				return fail();
			}
			if (dz != 0.0 && !probeBlockClamp(s, level, entity, blockers[AXIS_Z], AXIS_Z, dz, box)) {
				return fail();
			}

			// 所有有位移的轴都夹在 0.0，当前碰撞箱不会移动，原版仍会在同一判定下进入台阶重试。
			final boolean goingDown = dy < 0.0;
			if (maxUpStep > 0.0F && (goingDown || onGround) && (dx != 0.0 || dz != 0.0)
					&& !isAboveStepHeight(s, box, maxUpStep, dx, dz)
					&& !isStepUpVolumeEmpty(s, level, entity, box, movement, maxUpStep, onGround)) {
				return fail();
			}
			return success();
		} finally {
			s.ctxHolder[0] = null;
			s.inUse = false;
		}
	}

	private static Vec3 fail() {
		// 统计已关闭
		return null;
	}

	private static Vec3 success() {
		// 统计已关闭
		return Vec3.ZERO;
	}

	/* 针对当前碰撞箱，实时复核一个已缓存的阻挡方块坐标。 */
	private static boolean probeBlockClamp(
			Scratch s,
			Level level,
			Entity entity,
			long packedPos,
			int axis,
			double dist,
			AABB box
	) {
		if (packedPos == Long.MIN_VALUE) {
			return false;
		}
		final int bx = BlockPos.getX(packedPos);
		final int by = BlockPos.getY(packedPos);
		final int bz = BlockPos.getZ(packedPos);

		final BlockState state = getBlockStateForCollisionsSingle(level, bx, by, bz);
		if (state == null || state.isAir()) {
			return false;
		}
		final VoxelShape shape = getCollisionShape(s, level, entity, state, bx, by, bz);
		if (shape.isEmpty()) {
			return false;
		}
		if (shape.move((double) bx, (double) by, (double) bz).collide(AXES[axis], box, dist) != 0.0) {
			return false;
		}
		s.clampTop[axis] = (double) by + shape.max(Direction.Axis.Y);
		return true;
	}

	/*
	 * 所有被阻挡的水平轴都由一个顶面高于可到达最高台阶（box.minY + maxUpStep）的形状夹住时返回 true。
	 * 这样的形状在每个候选高度都会继续阻挡，台阶重试不可能成功，因此可以跳过 slab 查询。
	 */
	private static boolean isAboveStepHeight(Scratch s, AABB box, float maxUpStep, double dx, double dz) {
		final double maximumTop = box.minY + (double) maxUpStep;
		return (dx == 0.0 || s.clampTop[AXIS_X] > maximumTop)
				&& (dz == 0.0 || s.clampTop[AXIS_Z] > maximumTop);
	}

	/* 原版收集候选台阶高度的 slab 内没有任何碰撞体（实体形状、世界边界、方块）时返回 true。 */
	private static boolean isStepUpVolumeEmpty(
			Scratch s,
			Level level,
			Entity entity,
			AABB box,
			Vec3 movement,
			float maxUpStep,
			boolean onGround
	) {
		final double slabMinY = box.minY - (onGround ? 0.0 : 1.0E-5);
		final double slabMaxY = slabMinY + (double) maxUpStep;
		final AABB slab = new AABB(
				Math.min(box.minX, box.minX + movement.x), slabMinY, Math.min(box.minZ, box.minZ + movement.z),
				Math.max(box.maxX, box.maxX + movement.x), slabMaxY, Math.max(box.maxZ, box.maxZ + movement.z));

		final List<VoxelShape> entityCollisions = level.getEntityCollisions(entity, slab);
		if (!entityCollisions.isEmpty()) {
			final VoxelShape slabShape = Shapes.create(slab);
			for (int i = 0, n = entityCollisions.size(); i < n; i++) {
				if (Shapes.joinIsNotEmpty(entityCollisions.get(i), slabShape, BooleanOp.AND)) {
					return false;
				}
			}
		}
		final WorldBorder border = level.getWorldBorder();
		if (entity != null && border.isInsideCloseToBorder(entity, slab)) {
			return false;
		}
		for (final VoxelShape shape : level.getBlockCollisions(entity, slab)) {
			if (!shape.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static BlockState getBlockStateForCollisionsSingle(Level level, int bx, int by, int bz) {
		final net.minecraft.world.level.BlockGetter bg = level.getChunkForCollisions(bx >> 4, bz >> 4);
		if (bg == null || level.isOutsideBuildHeight(by)) {
			return null;
		}
		if (bg instanceof LevelChunk chunk) {
			final LevelChunkSection[] sections = chunk.getSections();
			final int index = level.getSectionIndex(by);
			if (index < 0 || index >= sections.length) {
				return null;
			}
			final LevelChunkSection section = sections[index];
			return section == null ? null : section.getBlockState(bx & 15, by & 15, bz & 15);
		}
		return bg.getBlockState(new BlockPos(bx, by, bz));
	}

	private static VoxelShape getCollisionShape(
			Scratch s, Level level, Entity entity, BlockState state, int bx, int by, int bz
	) {
		CollisionContext context = s.ctxHolder[0];
		if (context == null) {
			context = CollisionContext.of(entity);
			s.ctxHolder[0] = context;
		}
		return state.getCollisionShape(level, s.pos.set(bx, by, bz), context);
	}
}
