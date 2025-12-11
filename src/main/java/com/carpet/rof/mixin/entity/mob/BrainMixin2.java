package com.carpet.rof.mixin.entity.mob;


import com.carpet.rof.accessor.PiglinEntityAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

import static com.carpet.rof.ROFCarpetSettings.piglinMax;

@Mixin(value = Brain.class,priority = 2000)
public class BrainMixin2<E extends LivingEntity> {



    @Shadow @Final private Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks;

    @Shadow @Final private Set<Activity> possibleActivities;

    @Inject(method = "tickSensors",at = @At(value = "HEAD"),cancellable = true)
    public void tickSensors(ServerWorld world, E entity, CallbackInfo ci){
        if(entity instanceof PiglinEntity piglin){
            int count = ((PiglinEntityAccessor)piglin).getNearPiglinCount();
            if(!(RofTool.canLoadAi(piglin.getId(),count,piglinMax))) ci.cancel();
        }
    }



    @Inject(method = "startTasks",
            at = @At(value = "HEAD"),cancellable = true
    )
    public void startTasks2(ServerWorld world, E entity, CallbackInfo ci){
        if(entity instanceof PiglinEntity piglin) {

        }
    }
}

