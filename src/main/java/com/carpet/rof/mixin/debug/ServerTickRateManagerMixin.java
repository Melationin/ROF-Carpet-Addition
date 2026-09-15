package com.carpet.rof.mixin.debug;

import com.carpet.rof.debug.SprintTickTimer;
import net.minecraft.server.ServerTickRateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerTickRateManager.class)
public abstract class ServerTickRateManagerMixin
{
    @Shadow private long sprintTickStartTime;

    /** endTickWork 只在加速刻被调用一次，sprintTickStartTime 是原版在 checkShouldSprintThisTick 里记下的刻起点。 */
    @Inject(method = "endTickWork", at = @At("TAIL"))
    private void rof$reportSprintTick(CallbackInfo ci)
    {
        if (!SprintTickTimer.isRunning()) return;
        SprintTickTimer.onTickEnd(System.nanoTime() - this.sprintTickStartTime);
    }

    @Inject(method = "requestGameToSprint", at = @At("HEAD"))
    private void rof$finishReportOnRestart(int time, CallbackInfoReturnable<Boolean> cir)
    {
        SprintTickTimer.finish();
    }

    @Inject(method = "finishTickSprint", at = @At("HEAD"))
    private void rof$finishReport(CallbackInfo ci)
    {
        SprintTickTimer.finish();
    }
}
