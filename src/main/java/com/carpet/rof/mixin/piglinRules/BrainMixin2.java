package com.carpet.rof.mixin.piglinRules;


import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinMax;


@Mixin(value = Brain.class,priority = 2000)
public class BrainMixin2<E extends LivingEntity> {

    @Inject(method = "tickSensors",at = @At(value = "HEAD"),cancellable = true)
    public void tickSensors(ServerWorld world, E entity, CallbackInfo ci){
        if(entity instanceof PiglinEntity piglin){
            int count = ((PiglinEntityAccessor)piglin).getNearPiglinCount();
            if(!(RofTool.canLoadAi(piglin.getId(),count,piglinMax))) ci.cancel();
        }
    }
}

