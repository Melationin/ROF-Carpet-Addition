package com.carpet.rof.mixin.entity.mobAi;

import com.carpet.rof.entity.mobAi.MobAIUtil;
import net.minecraft.world.entity.ai.Brain;
//? if >=26.1 {
import net.minecraft.world.entity.ai.ActivityData;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.1 会包装每一次 ActivityData 创建；1.21.11 没有 ActivityData，因此改为钩住
// PiglinAi（以及其他所有生物）使用的 Brain 活动注册方法。
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
