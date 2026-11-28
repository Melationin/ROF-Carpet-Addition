package com.carpet.rof.mixin.entity.data;

import com.carpet.rof.entity.oec.OecSettings;
import com.carpet.rof.entity.data.LivingEntityHealthCacheAccess;
import com.carpet.rof.entity.data.SynchedDataCacheHooks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHealthCacheMixin implements LivingEntityHealthCacheAccess {
	@Shadow
	@Final
	private static EntityDataAccessor<Float> DATA_HEALTH_ID;

	@Shadow
	@Final
	private static EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID;

	@Unique
	private float rof$cachedHealth;
	@Unique
	private boolean rof$cachedSleeping;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void rof$captureLivingDataIds(CallbackInfo ci) {
		SynchedDataCacheHooks.registerHealthAccessor(DATA_HEALTH_ID);
		SynchedDataCacheHooks.registerSleepingPosAccessor(SLEEPING_POS_ID);
	}

	@WrapMethod(method = "getHealth")
	private float rof$fastGetHealth(Operation<Float> original) {
		if (!OecSettings.optimizedSynchedData) {
			return original.call();
		}

		return this.rof$cachedHealth;
	}

	@WrapMethod(method = "isSleeping")
	private boolean rof$fastIsSleeping(Operation<Boolean> original) {
		return this.rof$cachedSleeping;
	}

	@Override
	public void rof$setCachedHealth(float health) {
		this.rof$cachedHealth = health;
	}

	@Override
	public void rof$setCachedSleeping(boolean sleeping) {
		this.rof$cachedSleeping = sleeping;
	}
}
