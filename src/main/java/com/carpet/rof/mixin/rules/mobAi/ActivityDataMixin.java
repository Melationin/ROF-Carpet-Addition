package com.carpet.rof.mixin.rules.mobAi;

import com.carpet.rof.rules.mobAi.MobAIUtil;
import net.minecraft.world.entity.ai.Brain;
//? if >=26.1 {
import net.minecraft.world.entity.ai.ActivityData;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.1 wraps every ActivityData creation; 1.21.11 has no ActivityData, so it hooks the
// Brain activity registration methods that PiglinAi (and every other mob) uses instead.
//? if >=26.1 {
@Mixin(ActivityData.class)
//?}else{
/*@Mixin(Brain.class)
 *///?}
public class ActivityDataMixin
{
    //? if >=26.1 {
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void onInit(final CallbackInfo ci)
    {
        MobAIUtil.onActivityDataCreated();
    }
    //?}else{
    /*@Inject(method = {
            "addActivity(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;)V",
            "addActivityAndRemoveMemoryWhenStopped(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V"
    },
            at = @At(value = "TAIL"))
    private void onActivityAdded(final CallbackInfo ci)
    {
        MobAIUtil.onActivityDataCreated();
    }
    *///?}
}
