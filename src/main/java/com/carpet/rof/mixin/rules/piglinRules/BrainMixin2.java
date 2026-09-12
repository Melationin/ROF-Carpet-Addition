package com.carpet.rof.mixin.rules.piglinRules;


import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;


@Mixin(value = Brain.class,
       priority = 2000)
public class BrainMixin2<E extends LivingEntity>
{

    @Shadow @Final private Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority;

    @Shadow @Final private Set<Activity> activeActivities;

    @Inject(method = "tickSensors",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void tickSensors(ServerLevel world, E entity, CallbackInfo ci)
    {
        if (entity instanceof Piglin piglin) {
            if (((PiglinEntityAccessor) piglin).rof$getSuppressingAI())
                ci.cancel();
        }
    }

}

