package com.carpet.rof.mixin.event;

import com.carpet.rof.event.ROFEvents;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
//? <1.21.10 {
import net.minecraft.server.WorldGenerationProgressListenerFactory;
//?} else {
/*import net.minecraft.world.chunk.ChunkLoadProgress;
*///?}
import net.minecraft.util.ApiServices;

import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerMixin
{
    //? <1.21.10 {
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void startServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci)
    //?} else {
    /*@Inject(method = "<init>",at = @At(value = "TAIL"))
    private void startServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, ChunkLoadProgress chunkLoadProgress, CallbackInfo ci)
    *///?}
    {
        ROFEvents.ServerStart.run((MinecraftServer) (Object)this);
    }

    @Inject(method = "tick",
            at = @At(value = "TAIL"))
    private void tickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        ROFEvents.ServerTickEnd.run((MinecraftServer) (Object)this);
        ROFEvents.ServerTickEndTasks.run((MinecraftServer) (Object)this);

    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    private void tickBegin(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        ROFEvents.ServerTickBegin.run((MinecraftServer) (Object)this);
    }
}
