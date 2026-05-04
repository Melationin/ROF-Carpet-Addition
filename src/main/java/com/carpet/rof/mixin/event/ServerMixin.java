package com.carpet.rof.mixin.event;

import com.carpet.rof.event.ROFEvents;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
//? <1.21.9 {
/*import net.minecraft.server.WorldGenerationProgressListenerFactory;
*///?} else {
import net.minecraft.server.level.progress.LevelLoadListener;
//?}
import net.minecraft.server.Services;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerMixin
{

    @Inject(method = "tickServer",
            at = @At(value = "TAIL"))
    private void tickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        ROFEvents.ServerTickEnd.run((MinecraftServer) (Object)this);
        ROFEvents.ServerTickEndTasks.run((MinecraftServer) (Object)this);
    }

    @Inject(method = "tickServer",
            at = @At(value = "HEAD"))
    private void tickBegin(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        ROFEvents.ServerTickBegin.run((MinecraftServer) (Object)this);
    }

    @Inject(method = "saveAllChunks",
            at = @At(value = "TAIL"))
    private void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir)
    {
        ROFEvents.ServerSave.run((MinecraftServer) (Object)this);
    }
}
