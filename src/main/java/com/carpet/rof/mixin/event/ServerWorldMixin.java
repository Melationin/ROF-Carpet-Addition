package com.carpet.rof.mixin.event;

import com.carpet.rof.event.ROFEvents;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void init(CallbackInfo ci)
    {
        ROFEvents.WorldStart.run((ServerWorld) (Object)this);
    }

    @Inject(method = "tick",
            at = @At(value = "TAIL"))
    private void tickEnd(CallbackInfo ci)
    {
        ROFEvents.WorldTickEnd.run((ServerWorld) (Object)this);
        ROFEvents.WorldTickEndTasks.run((ServerWorld) (Object)this);
    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    private void tickBegin(CallbackInfo ci)
    {
        ROFEvents.WorldTickBegin.run((ServerWorld) (Object)this);
    }
}
