package com.carpet.rof.mixin.entity.data;

import com.carpet.rof.entity.data.SynchedDataCacheHooks;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataCacheMixin {
	@Shadow
	@Final
	private SyncedDataHolder entity;

	@Shadow
	@Final
	private SynchedEntityData.DataItem<?>[] itemsById;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void rof$onInit(SyncedDataHolder holder, SynchedEntityData.DataItem<?>[] items, CallbackInfo ci) {
		SynchedDataCacheHooks.initAll(this.entity, this.itemsById);
	}

	@Inject(
			method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;setValue(Ljava/lang/Object;)V",
					shift = At.Shift.AFTER
			)
	)
	private <T> void rof$onSet(EntityDataAccessor<T> accessor, T value, boolean force, CallbackInfo ci) {
		SynchedDataCacheHooks.syncValue(this.entity, accessor, value);
	}

	@Inject(
			method = "assignValue",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;setValue(Ljava/lang/Object;)V",
					shift = At.Shift.AFTER
			)
	)
	private <T> void rof$onAssignValue(
			SynchedEntityData.DataItem<T> item,
			SynchedEntityData.DataValue<?> value,
			CallbackInfo ci
	) {
		SynchedDataCacheHooks.syncValue(this.entity, item.getAccessor(), value.value());
	}
}
