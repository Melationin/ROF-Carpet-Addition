package com.carpet.rof.mixin.highChunkListener;



import com.carpet.rof.rules.highChunkListener.HighChunkSet;
import com.carpet.rof.utils.RofTool;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static com.carpet.rof.ROFCarpetServer.NETHER_HighChunkSet;
import static com.carpet.rof.rules.highChunkListener.HighChunkListenerSetting.highChunkListener;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {



    //? >=1.21.4 {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    //?} else {
    /*protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    *///?}
    @Inject(method = "save",at = @At(value = "HEAD"))
    void saveWorld(CallbackInfo ci){
        if(highChunkListener && (Object)this == NETHER_HighChunkSet.world){
            NETHER_HighChunkSet.Save();
            System.out.println("Saving High Chunks");
        }
    }
    @Inject(method = "<init>",at = @At(value = "RETURN"))
    void loadWorld(CallbackInfo ci){
        if( highChunkListener && RofTool.isNetherWorld(this)){
            NETHER_HighChunkSet = new HighChunkSet(129,(ServerWorld)(Object)this);
            NETHER_HighChunkSet.load();
            System.out.println(NETHER_HighChunkSet.size()+" Chunks Loaded");
        }
    }
    @Inject(method = "tick",at =@At(value = "HEAD"))
    void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(this.getTickManager().shouldTick()) {
            //System.out.println(this.getTime());
            if (highChunkListener && (Object) this == NETHER_HighChunkSet.world) {
                NETHER_HighChunkSet.update();
            }
        }
    }
}
