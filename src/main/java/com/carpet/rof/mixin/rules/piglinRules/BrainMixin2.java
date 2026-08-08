package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinOptimization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Brain.class, priority = 2000)
public abstract class BrainMixin2<E extends LivingEntity> {
    @Redirect(
            method = "tickSensors",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/sensing/Sensor;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    @SuppressWarnings("unchecked")
    private void rof$tickRequiredSensors(Sensor<E> sensor, ServerLevel world, E entity) {
        if (!(entity instanceof Piglin piglin)) {
            sensor.tick(world, entity);
        } else if (sensor instanceof NearestItemSensor) {
            PiglinOptimization.tickSharedNearestItemSensor(
                    (Sensor<? super Piglin>) (Sensor<?>) sensor,
                    world,
                    piglin
            );
        } else if (PiglinOptimization.shouldRunRegularAi(piglin)) {
            sensor.tick(world, entity);
        }
    }

    @Inject(method = {"startEachNonRunningBehavior", "tickEachRunningBehavior"},
            at = @At("HEAD"),
            cancellable = true)
    private void rof$spreadInactivePiglinBehaviors(ServerLevel world, E entity, CallbackInfo ci) {
        if (entity instanceof Piglin piglin
                && !PiglinOptimization.shouldTickBrainBehaviors(piglin, world.getGameTime())) {
            ci.cancel();
        }
    }
}
