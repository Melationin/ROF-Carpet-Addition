package com.carpet.rof.mixin.rules.enderPearl;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.EntityList;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
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
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World  {

    @Shadow  @Final
    private MinecraftServer server;

    @Shadow  @Final
    private ServerChunkManager chunkManager;

    @Shadow @Final  EntityList entityList;

    //? >=1.21.4 {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    //?} else {
    /*protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<?> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, (Supplier<Profiler>) profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    *///?}

    @Shadow public abstract void tickEntity(Entity entity);

    @Shadow public abstract TickManager getTickManager();

    @Shadow public abstract void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data);



    @Unique
    private boolean shouldBeForceLoaded(Entity entity){
        if(!entityList.has(entity)) return true;
        //? <=1.21.4 {
        /*return !this.chunkManager.chunkLoadingManager.getTicketManager().shouldTickEntities(entity.getChunkPos().toLong());
         *///?} else {
        return !this.chunkManager.chunkLoadingManager.getLevelManager().shouldTickEntities(entity.getChunkPos().toLong());
        //?}
    }

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    void ForceLoadedEntity(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(!server.getTickManager().shouldTick()) return;
        var forcedEntitylist = ExtraWorldDatas.fromWorld((ServerWorld)(Object)this).forcedEntitylist;
        forcedEntitylist.forEach(entity -> {
            if(shouldBeForceLoaded(entity)){
                if (!entity.isRemoved()) tickEntity(entity);
            }
        });
    }

}

