package com.carpet.rof.mixin.rules.enderPearl;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@SuppressWarnings("ConstantValue")
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level
{

    @Shadow  @Final
    private MinecraftServer server;

    @Shadow  @Final
    private ServerChunkCache chunkSource;

    @Shadow @Final
    EntityTickList entityTickList;

    //? >=1.21.2 {
    protected ServerLevelMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    //?} else {
    /*protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<?> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, (Supplier<Profiler>) profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    *///?}

    @Shadow public abstract void tickNonPassenger(Entity entity);

    @Shadow public abstract TickRateManager tickRateManager();

    @Shadow public abstract void blockEvent(BlockPos pos, Block block, int type, int data);

    @Unique
    private boolean shouldBeForceLoaded(Entity entity){
        if(!entityTickList.contains(entity)) return true;
        //? <=1.21.4 {
        /*return !this.chunkManager.chunkLoadingManager.getTicketManager().shouldTickEntities(entity.getChunkPos().toLong());
         *///?} else {
        return !this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().pack());
        //?}
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    void ForceLoadedEntity(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(!server.tickRateManager().runsNormally()) return;
        var forcedEntitylist = ExtraWorldDatas.fromWorld((ServerLevel)(Object)this).forcedEntitylist;
        forcedEntitylist.entrySet().removeIf(entry -> entry.getValue() == null||entry.getValue().isRemoved());
        forcedEntitylist.forEach((uuid,entity) -> {
            if(shouldBeForceLoaded(entity)){
                if (!entity.isRemoved()) tickNonPassenger(entity);
            }
        });
    }

}

