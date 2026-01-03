package com.carpet.rof.mixin.getBiomeLayerCache;

import com.carpet.rof.rules.getBiomeLayerCache.ServerWorldAccessor;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings("ConstantValue")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldAccessor {


    //? >=1.21.4 {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    //?} else {
    /*protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }
    *///?}


    @Shadow public abstract TickManager getTickManager();


    @Unique
    final HashMap<BlockPos,RegistryEntry<Biome>> LowYBiomeMap = new HashMap<>();

    @Unique
    private Chunk NowChunk = null;


    @Override
    public Chunk getNowChunk() {
        return NowChunk;
    }

    @Override
    public void setNowChunk(Chunk nowChunk) {
        NowChunk = nowChunk;
    }

}

