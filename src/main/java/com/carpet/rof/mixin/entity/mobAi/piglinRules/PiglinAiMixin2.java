package com.carpet.rof.mixin.entity.mobAi.piglinRules;

import com.carpet.rof.mixinAccessor.PiglinAccessor;
import com.carpet.rof.entity.mobAi.piglinRules.PiglinBCWrapper;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if >=26.1 {
import net.minecraft.world.entity.ai.ActivityData;
//?}
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin2
{

    @Inject(method = "wantsToPickup",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void cancelGather(Piglin piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        if (stack.getItem() == Items.GOLD_INGOT) {
            return;
        }
        if (PiglinAccessor.of(piglin).rof$getSuppressingAI()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    // 26.x 会把活动构建成 ActivityData 记录并把列表交给 Brain；
    // 1.21.11 则通过 Brain#addActivity 直接注册。两种方式都以相同顺序包装同一批行为列表
    //（core、idle、admireItem、fight、celebrate、retreat、rideHoglin），
    // 因此 PiglinBCWrapper 使用的活动/行为索引保持一致。
    //? if >=26.1 {
    @WrapOperation(method = {"initCoreActivity","initIdleActivity"},at = @At(value = "INVOKE",
                                                        target = "Lnet/minecraft/world/entity/ai/ActivityData;create(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;)Lnet/minecraft/world/entity/ai/ActivityData;"))
    private static ActivityData<Piglin> CoreAndIdleWrapper(Activity activity, int priorityOfFirstBehavior,
                                                     ImmutableList<? extends BehaviorControl<? super Piglin>> behaviorList,
                                                      Operation<ActivityData<Piglin>> original)
    {
        return original.call(activity, priorityOfFirstBehavior, rof$wrap(behaviorList));
    }

    @WrapOperation(method = {
            "initAdmireItemActivity",
            "initFightActivity",
            "initCelebrateActivity",
            "initRetreatActivity",
            "initRideHoglinActivity"
    },at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/ActivityData;create(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Lnet/minecraft/world/entity/ai/ActivityData;"))
    private static ActivityData<Piglin> OtherWrapper(Activity activity, int priorityOfFirstBehavior,
                                                          ImmutableList<? extends BehaviorControl<? super Piglin>> behaviorList,
                                                          MemoryModuleType<?> memoryToEraseWhenStopped,
                                                          Operation<ActivityData<Piglin>> original)
    {
        return original.call(activity, priorityOfFirstBehavior, rof$wrap(behaviorList), memoryToEraseWhenStopped);
    }
    //?}else{
    /*@WrapOperation(method = {"initCoreActivity","initIdleActivity"},at = @At(value = "INVOKE",
                                                        target = "Lnet/minecraft/world/entity/ai/Brain;addActivity(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;)V"))
    private static void rof$coreAndIdleWrapper(Brain<Piglin> brain, Activity activity, int priorityOfFirstBehavior,
                                               ImmutableList<? extends BehaviorControl<? super Piglin>> behaviorList,
                                               Operation<Void> original)
    {
        original.call(brain, activity, priorityOfFirstBehavior, rof$wrap(behaviorList));
    }

    @WrapOperation(method = {
            "initAdmireItemActivity",
            "initFightActivity",
            "initCelebrateActivity",
            "initRetreatActivity",
            "initRideHoglinActivity"
    },at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;addActivityAndRemoveMemoryWhenStopped(Lnet/minecraft/world/entity/schedule/Activity;ILcom/google/common/collect/ImmutableList;Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V"))
    private static void rof$otherWrapper(Brain<Piglin> brain, Activity activity, int priorityOfFirstBehavior,
                                                          ImmutableList<? extends BehaviorControl<? super Piglin>> behaviorList,
                                                          MemoryModuleType<?> memoryToEraseWhenStopped,
                                                          Operation<Void> original)
    {
        original.call(brain, activity, priorityOfFirstBehavior, rof$wrap(behaviorList), memoryToEraseWhenStopped);
    }
    *///?}


    @Unique
    private static ImmutableList<BehaviorControl<Piglin>> rof$wrap(ImmutableList<? extends BehaviorControl<? super Piglin>> behaviorList)
    {
        ImmutableList.Builder<BehaviorControl<Piglin>> builder = ImmutableList.builder();
        for (BehaviorControl<? super Piglin> behaviorControl : behaviorList)
        {
            builder.add(new PiglinBCWrapper((BehaviorControl<Piglin>) behaviorControl));
        }
        return builder.build();
    }
}
