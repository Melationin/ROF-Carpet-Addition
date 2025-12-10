package com.carpet.rof.mixin.entity.mob;

import com.carpet.rof.ROFCarpetSettings;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {


    @SuppressWarnings("ConstantValue")
    @Inject(method = "loot",at = @At(value = "HEAD"),cancellable = true)
    //? >=1.21.4 {
    private void loot(ServerWorld world, ItemEntity itemEntity, CallbackInfo ci) {
        if( (Object)this instanceof PiglinEntity && itemEntity.getItemAge()<= ROFCarpetSettings.piglinLootItemDelay) ci.cancel();
    }
    //?} else {
    /*private void loot(ItemEntity itemEntity, CallbackInfo ci) {
        if( (Object)this instanceof PiglinEntity && itemEntity.getItemAge()<= ROFCarpetSettings.piglinLootItemDelay) ci.cancel();
    }

    *///?}
}
