package com.carpet.rof.mixin.rules.piglinRules;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinLootItemDelay;

@Mixin(MobEntity.class)
public class MobEntityMixin
{


    @SuppressWarnings("ConstantValue")
    @Inject(method = "loot",
            at = @At(value = "HEAD"),
            cancellable = true)
            //? >=1.21.4 {
    private void loot(ServerWorld world, ItemEntity itemEntity, CallbackInfo ci)
    {
        if ((Object) this instanceof PiglinEntity && itemEntity.getItemAge() <= piglinLootItemDelay)
            ci.cancel();
    }
    //?} else {
    /*private void loot(ItemEntity itemEntity, CallbackInfo ci) {
        if( (Object)this instanceof PiglinEntity && itemEntity.getItemAge()<= piglinLootItemDelay) ci.cancel();
    }

    *///?}
}
