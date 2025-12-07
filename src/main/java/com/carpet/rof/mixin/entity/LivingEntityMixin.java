package com.carpet.rof.mixin.entity;

import com.carpet.rof.accessor.PiglinEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.ROFCarpetSettings.piglinMax;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel",at = @At(value = "HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        if((Object)this instanceof PiglinEntity){
            int count = ((PiglinEntityAccessor)(Object)this).getNearPiglinCount();
            if(count>=piglinMax && Math.random()*count>=1){
                ci.cancel();
            }
        }
    }
}
