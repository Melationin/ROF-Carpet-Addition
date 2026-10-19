package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
import com.carpet.rof.carpet.fakePlayerTick.FakeScoreAccess;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin
{
    @Inject(
            method = "getOrCreatePlayerScore(Lnet/minecraft/world/scores/ScoreHolder;Lnet/minecraft/world/scores/Objective;Z)Lnet/minecraft/world/scores/ScoreAccess;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rof$skipFakePlayerScore(ScoreHolder holder, Objective objective, boolean forceWritable, CallbackInfoReturnable<ScoreAccess> cir)
    {
        if (FakePlayerTickUtil.isOptimized(holder))
        {
            cir.setReturnValue(FakeScoreAccess.INSTANCE);
        }
    }
}
