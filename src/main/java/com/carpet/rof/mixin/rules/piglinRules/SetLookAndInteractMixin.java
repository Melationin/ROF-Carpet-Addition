package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinOptimization;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetLookAndInteract.class)
public abstract class SetLookAndInteractMixin
{
    @Inject(method = "lambda$create$2",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void onFindInteractionTargetTask(BehaviorBuilder.Instance taskContext, MemoryAccessor memoryQueryResult, int i, EntityType entityType, MemoryAccessor memoryQueryResult2, MemoryAccessor memoryQueryResult3, ServerLevel world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir)
    {
        if (entity instanceof Piglin piglin) {
            if (!PiglinOptimization.shouldRunRegularAi(piglin)) {
                cir.setReturnValue(false);
            }
        }
    }

}
