package com.carpet.rof.mixin.entity.data;

import com.carpet.rof.entity.oec.OecSettings;
import com.carpet.rof.entity.data.EntitySharedFlagsCacheAccess;
import com.carpet.rof.entity.data.SynchedDataCacheHooks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntitySharedFlagsCacheMixin implements EntitySharedFlagsCacheAccess {
	@Shadow
	@Final
	protected static EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;

	@Unique
	private byte rof$cachedSharedFlags;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void rof$captureSharedFlagsId(CallbackInfo ci) {
		SynchedDataCacheHooks.registerSharedFlagsAccessor(DATA_SHARED_FLAGS_ID);
	}

	@WrapMethod(method = "getSharedFlag")
	private boolean rof$fastGetSharedFlag(int flag, Operation<Boolean> original) {
		if (!OecSettings.optimizedSynchedData) {
			return original.call(flag);
		}

		return (this.rof$cachedSharedFlags & (1 << flag)) != 0;
	}

	@Override
	public void rof$setCachedSharedFlags(byte flags) {
		this.rof$cachedSharedFlags = flags;
	}
}
