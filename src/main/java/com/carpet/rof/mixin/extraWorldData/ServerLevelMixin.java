package com.carpet.rof.mixin.extraWorldData;



import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.ROFTool;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.blockingEnderPearlLoading;
import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;
import com.carpet.rof.utils.ChunkPosHelper;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements IExtraChunkDataAccessor
{


    @Shadow public abstract String toString();

    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    ExtraWorldDatas ROFextraWorldDatas;

    @Override
    public ExtraWorldDatas getExtraChunkDatas()
    {
        return ROFextraWorldDatas;
    }


    @Inject(method = "save",
            at = @At(value = "TAIL"))
    void saveWorld(CallbackInfo ci)
    {
        if(exceedChunkMarker )
       ROFTool.saveNBT2Data((ServerLevel) (Object)this,"extraWorldData.dat", ROFextraWorldDatas.toNbt());
    }


    @Shadow  @Final
    private ServerChunkCache chunkSource;

    @Shadow @Final
    private EntityTickList entityTickList;

    @Shadow
    @Final
    private PersistentEntitySectionManager<Entity> entityManager;

    @Shadow
    public abstract void tickNonPassenger(Entity entity);


    @Unique
    private boolean shouldBeForceLoaded(Entity entity)
    {
        if (!entityTickList.contains(entity))
            return true;
        //? <=1.21.4 {
        /*return !this.chunkManager.chunkLoadingManager.getTicketManager().shouldTickEntities(entity.getChunkPos().toLong());
         *///?} else {
        return !this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPosHelper.pack(entity.chunkPosition()));
        //?}
    }


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    void ForceLoadedEntity(BooleanSupplier haveTime, CallbackInfo ci){

        ServerLevel level = (ServerLevel)(Object)this;

        var tickChunkList = ExtraWorldDatas.fromWorld(level).enderPearlForcedSyncChunks;

        MinecraftServer server = level.getServer();
        long[] snapshot = tickChunkList .toLongArray();            // 快照迭代，避免并发修改
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(blockingEnderPearlLoading);
        ServerChunkCache chunkSource = level.getChunkSource();
        ChunkMap chunkMap = chunkSource.chunkMap;
        // ★ 关键前置：让票据等级真正生效（见 §三.3）

        int it[] = new int[1];
        it[0] = 0;
        server.managedBlock(() ->
        {
            if (System.nanoTime() >= deadline) {
                //ROFTool.rDEBUG("b: deadline");
                return true;
            }
            level.entityManager.processPendingLoads();
            for(;it[0] < snapshot.length; ) {
                long key = snapshot[it[0]];

                boolean simulationReady = chunkMap.getDistanceManager().inEntityTickingRange(key);
                if (!simulationReady) {
                    //ROFTool.rDEBUG("b: simulationReady");
                    return true;
                }
                ChunkHolder holder = chunkMap.getUpdatingChunkIfPresent(key);
                if (holder != null && holder.getFullStatus().isOrAfter(FullChunkStatus.ENTITY_TICKING)) {
                    ChunkResult<LevelChunk> result = holder.getEntityTickingChunkFuture().getNow(null);

                    if (result == null) {
                        return false;
                    }

                }
                boolean visibilityReady = entityManager.isTicking(ChunkPosHelper.unpack(key));

                boolean entitiesLoaded = entityManager.areEntitiesLoaded(key);

                if(!visibilityReady || !entitiesLoaded) return false;
                ++it[0];
            }
            return true;
        });

        // 只摘掉已就绪的；未就绪的留在表里，下一 tick 再试（或按你的策略丢弃并记日志）
        for (long key : snapshot) {
            if (level.areEntitiesLoaded(key)) tickChunkList.remove(key);
        }

        if(!this.server.tickRateManager().runsNormally()) return;
        var forcedEntitylist = ExtraWorldDatas.fromWorld((ServerLevel)(Object)this).forcedEntitylist;

        forcedEntitylist.entrySet().removeIf(entry -> entry.getValue() == null||entry.getValue().isRemoved());
        forcedEntitylist.forEach((uuid,entity) -> {
            if(shouldBeForceLoaded(entity)){
                if (!entity.isRemoved()) tickNonPassenger(entity);
            }
        });
    }

    @Unique
    private boolean isReadyToEntityTick(ServerLevel level, long key) {
        ChunkPos cp = ChunkPosHelper.unpack(key);          // ChunkPosHelper.java:32 → ChunkPos.unpack(long)
        return level.entityManager.canPositionTick(cp)                                        // A：可见性 TICKING
                && level.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(key) // B：模拟票 ≤31
                && level.areEntitiesLoaded(key);                                                  // C：数据已入库
    }



    @Inject(method = "<init>",
            at = @At(value = "RETURN"))
    void loadWorld(CallbackInfo ci)
    {
        ROFextraWorldDatas = new ExtraWorldDatas();
        try {
            Path savaPath = ROFTool.getSavePath((ServerLevel) (Object) this).resolve("data").resolve("extraWorldData.dat");
            CompoundTag nbtCompound;
            if(ROFIO.isGzip(savaPath)){
                nbtCompound= NbtIo.readCompressed(savaPath, NbtAccounter.create(104857600L));
            }else {
                nbtCompound= NbtIo.read(savaPath);
            }
            if(nbtCompound == null){
                if(ROFTool.isNetherWorld((ServerLevel) (Object) this)){
                    ROFextraWorldDatas.exceedChunkMarker.topY = 128;
                }else {
                    ROFextraWorldDatas.exceedChunkMarker.topY = Integer.MAX_VALUE/2;
                }
            }else {
                ROFextraWorldDatas.read(nbtCompound);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick",
            at = @At(value = "HEAD"))
    void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {
        if (((ServerLevel)(Object)this).tickRateManager().runsNormally()&&exceedChunkMarker) {
            this.ROFextraWorldDatas.exceedChunkMarker.update((ServerLevel) (Object) this);
        }
    }
}
