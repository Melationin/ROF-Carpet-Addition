package com.carpet.rof.mixin.world;

import com.carpet.rof.ROFCarpetSettings;
import com.carpet.rof.accessor.ServerWorldAccessor;
import com.carpet.rof.utils.HighChunkSet;
import com.carpet.rof.utils.RofTool;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.EntityList;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static com.carpet.rof.ROFCarpetServer.NETHER_HighChunkSet;

@SuppressWarnings("ConstantValue")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldAccessor {

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
    /*protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    *///?}

    @Shadow public abstract void tickEntity(Entity entity);

    @Shadow @Final private boolean shouldTickTime;

    @Shadow public abstract TickManager getTickManager();

    @Shadow @Final public ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue;

    @Shadow public abstract void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data);

    @Unique
    final EntityList OtherEntitylist = new EntityList();

    @Unique
    final HashMap<RofTool.EntityPosAndVec, TntEntity> TNTMergeMap = new HashMap<>();

    @Unique
    final HashMap<BlockPos,RegistryEntry<Biome>> LowYBiomeMap = new HashMap<>();

    @Unique
    private boolean shouldBeOtherTick(Entity entity){
        if(!entityList.has(entity)) return true;
        //? <=1.21.4 {
        /*return !this.chunkManager.chunkLoadingManager.getTicketManager().shouldTickEntities(entity.getChunkPos().toLong());
        *///?} else {
        return !this.chunkManager.chunkLoadingManager.getLevelManager().shouldTickEntities(entity.getChunkPos().toLong());
        //?}




    }

    @Unique
    private Chunk NowChunk = null;

    //region Accessor
    @Override
    public void addMustTickEntity(Entity entity){
        OtherEntitylist.add(entity);
    }

    @Override
    public void  removeMustTickEntity(Entity entity){
        OtherEntitylist.remove(entity);
    }

    @Override
    public boolean  inMustTickEntity(Entity entity){
        return  OtherEntitylist.has(entity);
    }

    @Override
    public HashMap<RofTool.EntityPosAndVec, TntEntity> getTNTMergeMap(){
        return TNTMergeMap;
    }

    @Override
    public Chunk getNowChunk() {
        return NowChunk;
    }

    @Override
    public void setNowChunk(Chunk nowChunk) {
        NowChunk = nowChunk;
    }


    @Inject(method = "tick",at = @At(value = "HEAD"))
    void clearTNTMergeMap(CallbackInfo ci){
        TNTMergeMap.clear();
    }

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    void MustTickEntity(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(!server.getTickManager().shouldTick()) return;
        OtherEntitylist.forEach(entity -> {
            if(shouldBeOtherTick(entity)){
                if (!entity.isRemoved()) tickEntity(entity);
            }
        });
    }


    @Inject(method = "save",at = @At(value = "HEAD"))
    void saveWorld(CallbackInfo ci){
        if(ROFCarpetSettings.highChunkListener && (Object)this == NETHER_HighChunkSet.world){
            NETHER_HighChunkSet.Save();

            System.out.println("Saving High Chunks");
        }
    }
    @Inject(method = "<init>",at = @At(value = "RETURN"))
    void loadWorld(CallbackInfo ci){
        if( ROFCarpetSettings.highChunkListener && RofTool.isNetherWorld(this)){
            NETHER_HighChunkSet = new HighChunkSet(129,(ServerWorld)(Object)this);
            NETHER_HighChunkSet.load();


            System.out.println(NETHER_HighChunkSet.size()+" Chunks Loaded");
        }
    }
    @Inject(method = "tick",at =@At(value = "HEAD"))
    void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(this.getTickManager().shouldTick()) {
            //System.out.println(this.getTime());
            if (ROFCarpetSettings.highChunkListener && (Object) this == NETHER_HighChunkSet.world) {
                NETHER_HighChunkSet.update();
            }
        }
    }

}

