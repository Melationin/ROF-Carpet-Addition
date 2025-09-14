package com.carpet.rof.mixin.entity.mob;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Brain.class)
public class BrainMixin2<E extends LivingEntity> {
    @Inject(method = "tickSensors",at = @At(value = "HEAD"),cancellable = true)
    public void tickSensors(ServerWorld world, E entity, CallbackInfo ci){
        if(entity instanceof PiglinEntity) ci.cancel();
    }


}
