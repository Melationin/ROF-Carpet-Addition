package com.carpet.rof.mixin.entity.mob;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin2 {
    @Inject(at = @At(value = "TAIL"),method = "create" )
    private static void piglinBrain(PiglinEntity piglin, Brain<PiglinEntity> brain, CallbackInfoReturnable<Brain<?>> cir) {
        //brain.getPossibleActivities().clear();
        //brain.getPossibleActivities().add(RemoveOffHandItemTask.create());
    }

    @Inject(method = "tickActivities",at = @At(value = "HEAD"),cancellable = true)
    private static void piglinBrain(PiglinEntity piglin, CallbackInfo ci) {
    }
}

