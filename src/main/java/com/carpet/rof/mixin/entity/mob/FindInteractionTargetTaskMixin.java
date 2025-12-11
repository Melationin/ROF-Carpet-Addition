package com.carpet.rof.mixin.entity.mob;

import com.carpet.rof.ROFCarpetSettings;
import com.carpet.rof.accessor.PiglinEntityAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.ROFCarpetSettings.piglinMax;

@Mixin(FindInteractionTargetTask.class)
public abstract class FindInteractionTargetTaskMixin {
    @Inject(method = "method_47085",at = @At(value = "HEAD"),cancellable = true)
    private static void onFindInteractionTargetTask(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, int i, EntityType entityType, MemoryQueryResult memoryQueryResult2, MemoryQueryResult memoryQueryResult3, ServerWorld world, LivingEntity entity, long time, CallbackInfoReturnable<Boolean> cir)
    {
        if(entity instanceof PiglinEntity piglin){
            int count = ((PiglinEntityAccessor)piglin).getNearPiglinCount();
            if(!RofTool.canLoadAi(entity.getId(),count, piglinMax)){
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
