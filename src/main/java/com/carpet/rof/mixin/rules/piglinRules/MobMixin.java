package com.carpet.rof.mixin.rules.piglinRules;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinLootItemDelay;

@Mixin(Mob.class)
public class MobMixin
{


    @SuppressWarnings("ConstantValue")
    @Inject(method = "pickUpItem",
            at = @At(value = "HEAD"),
            cancellable = true)
            //? >=1.21.4 {
    private void loot(ServerLevel world, ItemEntity itemEntity, CallbackInfo ci)
    {
        if ((Object) this instanceof Piglin && itemEntity.getAge() <= piglinLootItemDelay)
            ci.cancel();
    }
    //?} else {
    /*private void loot(ItemEntity itemEntity, CallbackInfo ci) {
        if( (Object)this instanceof PiglinEntity && itemEntity.getItemAge()<= piglinLootItemDelay) ci.cancel();
    }

    *///?}
}
