package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(SimpleCriterionTrigger.class)
public abstract class SimpleCriterionTriggerMixin
{
    @Inject(
            method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Predicate;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rof$skipFakePlayerTrigger(ServerPlayer player, Predicate<?> matcher, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized(player))
        {
            ci.cancel();
        }
    }
}
