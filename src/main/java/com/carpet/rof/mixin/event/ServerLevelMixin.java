package com.carpet.rof.mixin.event;

import com.carpet.rof.event.ROFEvents;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void init(CallbackInfo ci)
    {
        ROFEvents.WorldStart.run((ServerLevel) (Object)this);
    }

    @Inject(method = "tick",
            at = @At(value = "TAIL"))
    private void tickEnd(CallbackInfo ci)
    {
        ROFEvents.WorldTickEnd.run((ServerLevel) (Object)this);
        ROFEvents.WorldTickEndTasks.run((ServerLevel) (Object)this);
    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    private void tickBegin(CallbackInfo ci)
    {
        ROFEvents.WorldTickBegin.run((ServerLevel) (Object)this);
    }
}
