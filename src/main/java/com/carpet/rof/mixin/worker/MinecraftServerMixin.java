package com.carpet.rof.mixin.worker;

import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
    @Inject(method = "startServer", at = @At(value = "TAIL"))
    private static <S> void startServer(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cir)
    {
        SingleTaskWorker.INSTANCE.start();
    }
}
