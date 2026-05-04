package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;


@Mixin(SetEntityLookTarget.class)
public abstract class SetEntityLookTargetMixin
{
    @Inject(method = "lambda$create$5",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void method_47063(BehaviorBuilder.Instance taskContext, MemoryAccessor memoryQueryResult, Predicate predicate, float f, MemoryAccessor memoryQueryResult2, ServerLevel world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir)
    {
        if (entity instanceof Piglin piglin) {
            int count = ((PiglinEntityAccessor) piglin).getNearPiglinCount();
            if (!ROFTool.canLoadAi(entity.getId(), count, piglinStackingAISuppression)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
