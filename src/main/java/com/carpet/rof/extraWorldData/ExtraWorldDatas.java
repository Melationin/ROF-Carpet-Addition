package com.carpet.rof.extraWorldData;

import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkEntitySpawnLogger;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkLoadedFinder;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.extraWorldData.asyncWorldgen.AsyncWorldgenCacheData;
import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import com.carpet.rof.rules.merge.MergeSetting;
import com.carpet.rof.utils.NBTData;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtraWorldDatas implements NBTData
{
    public ExceedChunkMarker exceedChunkMarker = new ExceedChunkMarker(Integer.MAX_VALUE/2);

    public ChunkLoadedFinder chunkLoadedFinder = new ChunkLoadedFinder();

    public final Map<UUID, Entity> forcedEntitylist = new HashMap<>();

    public final HashMap<MergeSetting.EntityPosAndVec, PrimedTnt> mergeTntMap =  new HashMap<>();

    public final Map<EntityType<?>,Integer> entitySpawnCountsPerTick = new HashMap<>();

    public final ChunkEntitySpawnLogger  chunkEntitySpawnLogger = new ChunkEntitySpawnLogger();

    public final LongOpenHashSet enderPearlForcedSyncChunks = new LongOpenHashSet();


    /** Runtime-only, per-dimension chunk-list caches. Never serialized to world data. */
    public final AsyncWorldgenCacheData asyncWorldgenCache = new AsyncWorldgenCacheData();

    public static ExtraWorldDatas fromWorld(ServerLevel world){
        return  ((IExtraChunkDataAccessor)world).getExtraChunkDatas();
    }


    @Override
    public void write(CompoundTag nbt)
    {
        if(ExceedChunkMarkerSetting.exceedChunkMarker){
            nbt.put(exceedChunkMarker.getName(), exceedChunkMarker.toNbt());
        }
    }

    @Override
    public void read(CompoundTag nbt)
    {
        //? if >1.21.4 {
        if(ExceedChunkMarkerSetting.exceedChunkMarker) nbt.getCompound(exceedChunkMarker.getName()).ifPresent(
                nbtCompound->  exceedChunkMarker.read(nbtCompound));

        //?} else {
        /*if(ExceedChunkMarkerSetting.exceedChunkMarker){
            if(nbt.contains(exceedChunkMarker.getName())){
                exceedChunkMarker.read(nbt.getCompound(exceedChunkMarker.getName()));
            }
        }
        *///?}
    }

    public CompoundTag toNbt(){
       CompoundTag nbt = new CompoundTag();
       write(nbt);
       return nbt;
   }
}
