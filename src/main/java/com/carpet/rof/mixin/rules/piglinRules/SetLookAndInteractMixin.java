package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
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

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;


@Mixin(SetLookAndInteract.class)
public abstract class SetLookAndInteractMixin
{
    @Inject(method = "lambda$create$2",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void onFindInteractionTargetTask(BehaviorBuilder.Instance taskContext, MemoryAccessor memoryQueryResult, int i, EntityType entityType, MemoryAccessor memoryQueryResult2, MemoryAccessor memoryQueryResult3, ServerLevel world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir)
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
