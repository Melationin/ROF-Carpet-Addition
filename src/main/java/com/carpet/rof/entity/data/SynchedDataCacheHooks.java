package com.carpet.rof.entity.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;

import java.util.Optional;

/*
 * mixin.entity.synched_data_cache 的集中注册与同步钩子。
 *
 * 访问器实例由 Entity、LivingEntity、Piglin 上的 <clinit> @At("TAIL") 钩子延迟发布，
 * 因此本类不会触发 SynchedEntityData.defineId 的过早注册顺序问题。
 */
public final class SynchedDataCacheHooks {
	public static volatile EntityDataAccessor<Byte> SHARED_FLAGS_ID;
	public static volatile EntityDataAccessor<Float> HEALTH_ID;
	public static volatile EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID;
	public static volatile EntityDataAccessor<Boolean> PIGLIN_BABY_ID;

	private SynchedDataCacheHooks() {
	}

	public static void registerSharedFlagsAccessor(EntityDataAccessor<Byte> accessor) {
		SHARED_FLAGS_ID = accessor;
	}

	public static void registerHealthAccessor(EntityDataAccessor<Float> accessor) {
		HEALTH_ID = accessor;
	}

	public static void registerSleepingPosAccessor(EntityDataAccessor<Optional<BlockPos>> accessor) {
		SLEEPING_POS_ID = accessor;
	}

	public static void registerPiglinBabyAccessor(EntityDataAccessor<Boolean> accessor) {
		PIGLIN_BABY_ID = accessor;
	}

	public static void initAll(SyncedDataHolder entity, SynchedEntityData.DataItem<?>[] itemsById) {
		if (!(entity instanceof Entity)) {
			return;
		}
		final EntityDataAccessor<Byte> flagsId = SHARED_FLAGS_ID;
		if (flagsId != null && flagsId.id() < itemsById.length) {
			final SynchedEntityData.DataItem<?> item = itemsById[flagsId.id()];
			if (item != null && item.getValue() instanceof Byte b) {
				((EntitySharedFlagsCacheAccess) entity).rof$setCachedSharedFlags(b);
			}
		}
		if (entity instanceof LivingEntity) {
			final LivingEntityHealthCacheAccess livingAccess = (LivingEntityHealthCacheAccess) entity;
			final EntityDataAccessor<Float> healthId = HEALTH_ID;
			if (healthId != null && healthId.id() < itemsById.length) {
				final SynchedEntityData.DataItem<?> item = itemsById[healthId.id()];
				if (item != null && item.getValue() instanceof Float f) {
					livingAccess.rof$setCachedHealth(f);
				}
			}
			final EntityDataAccessor<Optional<BlockPos>> sleepingId = SLEEPING_POS_ID;
			if (sleepingId != null && sleepingId.id() < itemsById.length) {
				final SynchedEntityData.DataItem<?> item = itemsById[sleepingId.id()];
				if (item != null && item.getValue() instanceof Optional<?> opt) {
					livingAccess.rof$setCachedSleeping(opt.isPresent());
				}
			}
		}
		if (entity instanceof Piglin) {
			final EntityDataAccessor<Boolean> babyId = PIGLIN_BABY_ID;
			if (babyId != null && babyId.id() < itemsById.length) {
				final SynchedEntityData.DataItem<?> item = itemsById[babyId.id()];
				if (item != null && item.getValue() instanceof Boolean b) {
					((PiglinBabyCacheAccess) entity).rof$setCachedBaby(b);
				}
			}
		}
	}

	public static void syncValue(SyncedDataHolder entity, EntityDataAccessor<?> accessor, Object value) {
		if (accessor == SHARED_FLAGS_ID) {
			if (entity instanceof EntitySharedFlagsCacheAccess access && value instanceof Byte b) {
				access.rof$setCachedSharedFlags(b);
			}
		} else if (accessor == HEALTH_ID) {
			if (entity instanceof LivingEntityHealthCacheAccess access && value instanceof Float f) {
				access.rof$setCachedHealth(f);
			}
		} else if (accessor == SLEEPING_POS_ID) {
			if (entity instanceof LivingEntityHealthCacheAccess access && value instanceof Optional<?> opt) {
				access.rof$setCachedSleeping(opt.isPresent());
			}
		} else if (accessor == PIGLIN_BABY_ID) {
			if (entity instanceof PiglinBabyCacheAccess access && value instanceof Boolean b) {
				access.rof$setCachedBaby(b);
			}
		}
	}
}
