package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinOptimization;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "pushEntities", at = @At("HEAD"), cancellable = true)
    private void rof$skipInactivePiglinCollisionScan(CallbackInfo ci) {
        if ((Object) this instanceof Piglin piglin
                && !PiglinOptimization.shouldUseVanillaMovement(piglin)) {
            ci.cancel();
        }
    }
}
