package com.carpet.rof.mixin.entity.data;

import com.carpet.rof.entity.oec.OecSettings;
import com.carpet.rof.entity.data.PiglinBabyCacheAccess;
import com.carpet.rof.entity.data.SynchedDataCacheHooks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
public abstract class PiglinBabyCacheMixin implements PiglinBabyCacheAccess {
	@Shadow
	@Final
	private static EntityDataAccessor<Boolean> DATA_BABY_ID;

	@Unique
	private boolean rof$cachedBaby;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void rof$capturePiglinBabyId(CallbackInfo ci) {
		SynchedDataCacheHooks.registerPiglinBabyAccessor(DATA_BABY_ID);
	}

	@WrapMethod(method = "isBaby")
	private boolean rof$fastIsBaby(Operation<Boolean> original) {
		if (!OecSettings.optimizedSynchedData) {
			return original.call();
		}

		return this.rof$cachedBaby;
	}

	@Override
	public void rof$setCachedBaby(boolean baby) {
		this.rof$cachedBaby = baby;
	}
}
