package com.carpet.rof.mixin.world;


import com.carpet.rof.accessor.ServerWorldAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = ServerChunkManager.class,priority = 2000)
public abstract class ServerChunkManagerMixin {

    @Shadow public abstract World getWorld();


    //region tickSpawn
    //? >=1.21.7 {
    /*@Inject(method = "tickSpawningChunk",at = @At(value = "HEAD"))
    public void addNowChunk1(WorldChunk chunk, long timeDelta, List<SpawnGroup> spawnableGroups, SpawnHelper.Info info, CallbackInfo ci){
        ((ServerWorldAccessor) this.getWorld()).setNowChunk(chunk);
    }

    @Inject(method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;J)V",at = @At(value = "TAIL"))
    public void tickChunks(Profiler profiler, long timeDelta, CallbackInfo ci){
        ((ServerWorldAccessor) this.getWorld()).setNowChunk(null);
    }
    *///?} else if >=1.21.4 {
    @Inject(method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;JLjava/util/List;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/WorldChunk;increaseInhabitedTime(J)V"
            ))
    public void addNowChunk1(Profiler profiler, long timeDelta, List<WorldChunk> chunks, CallbackInfo ci, @Local(ordinal = 0) WorldChunk worldChunk) {
        ((ServerWorldAccessor) this.getWorld()).setNowChunk(worldChunk);
    }

    @Inject(method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;JLjava/util/List;)V",
            at = @At(value = "TAIL"))
    public void tickChunks(Profiler profiler, long timeDelta, List<WorldChunk> chunks, CallbackInfo ci) {
        ((ServerWorldAccessor) this.getWorld()).setNowChunk(null);
    }

    //?} else {





    //?}

    //endregion


}
