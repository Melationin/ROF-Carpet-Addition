package com.carpet.rof.mixin.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel",at = @At(value = "HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
      //  if((Object)this instanceof PiglinEntity) ci.cancel();
    }
}
