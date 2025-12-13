package com.carpet.rof.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow @Final private ServerTickManager tickManager;

    @Inject(method = "runServer",at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", shift = At.Shift.AFTER) )
    public void runServer(CallbackInfo ci){
        this.tickManager.setFrozen(true);
    }
}
