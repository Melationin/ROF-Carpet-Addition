package com.carpet.rof.extraWorldData;

import com.carpet.rof.accessor.IExtraChunkDataAccessor;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkEntitySpawnLogger;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkModifyData;
import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.extraWorldData.extraChunkDatas.LoadedChunkManager;
import com.carpet.rof.rules.extraChunkDatas.ChunkModifySetting;
import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import com.carpet.rof.rules.mergeTNTNext.MergeTNTNextSetting;
import com.carpet.rof.utils.NBTData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityList;

import java.util.HashMap;
import java.util.Map;

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

    public ChunkModifyData chunkModifyData = new ChunkModifyData(100);

    public LoadedChunkManager loadedChunkManager = new LoadedChunkManager();

    public final EntityList forcedEntitylist = new EntityList();

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
        if(ChunkModifySetting.chunkModifyLogger){
            nbt.put(chunkModifyData.getName(), chunkModifyData.toNbt());
        }
    }

    @Override
    public void read(NbtCompound nbt)
    {
        //? if >1.21.4 {
        if(ExceedChunkMarkerSetting.exceedChunkMarker) nbt.getCompound(exceedChunkMarker.getName()).ifPresent(
                nbtCompound->  exceedChunkMarker.read(nbtCompound));

        if(chunkModifyData.enable()) nbt.getCompound(chunkModifyData.getName()).ifPresent(
                nbtCompound -> chunkModifyData.read(nbtCompound));
        //?} else {
        /*if(ExceedChunkMarkerSetting.exceedChunkMarker){
            if(nbt.contains(exceedChunkMarker.getName())){
                exceedChunkMarker.read(nbt.getCompound(exceedChunkMarker.getName()));
            }
            if(nbt.contains(chunkModifyData.getName())){
                chunkModifyData.read(nbt.getCompound(chunkModifyData.getName()));
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
