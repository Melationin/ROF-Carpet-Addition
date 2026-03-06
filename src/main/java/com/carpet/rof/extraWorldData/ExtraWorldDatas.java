package com.carpet.rof.extraWorldData;

import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkEntitySpawnLogger;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkLoadedFinder;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import com.carpet.rof.rules.mergeTNTNext.MergeTNTNextSetting;
import com.carpet.rof.utils.NBTData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtraWorldDatas implements NBTData
{
    static {
        ROFEvents.WorldTickBegin.register(world -> {
            ExtraWorldDatas.fromWorld(world).entitySpawnCountsPerTick.clear();
            ExtraWorldDatas.fromWorld(world).mergeTntMap.clear();
            ExtraWorldDatas.fromWorld(world).chunkEntitySpawnLogger.run(world);
        });
    }

    public ExceedChunkMarker exceedChunkMarker = new ExceedChunkMarker(Integer.MAX_VALUE/2);

    public ChunkLoadedFinder chunkLoadedFinder = new ChunkLoadedFinder();

    public final Map<UUID, Entity> forcedEntitylist = new HashMap<>();

    public final HashMap<MergeTNTNextSetting.EntityPosAndVec, TntEntity> mergeTntMap =  new HashMap<>();

    public final Map<EntityType<?>,Integer> entitySpawnCountsPerTick = new HashMap<>();

    public final ChunkEntitySpawnLogger  chunkEntitySpawnLogger = new ChunkEntitySpawnLogger();

    public static ExtraWorldDatas fromWorld(ServerWorld world){
        return  ((IExtraChunkDataAccessor)world).getExtraChunkDatas();
    }


    @Override
    public void write(NbtCompound nbt)
    {
        if(ExceedChunkMarkerSetting.exceedChunkMarker){
            nbt.put(exceedChunkMarker.getName(), exceedChunkMarker.toNbt());
        }
    }

    @Override
    public void read(NbtCompound nbt)
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

    public NbtCompound toNbt(){
       NbtCompound nbt = new NbtCompound();
       write(nbt);
       return nbt;
   }
}
