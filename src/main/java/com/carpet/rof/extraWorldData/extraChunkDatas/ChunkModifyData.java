package com.carpet.rof.extraWorldData.extraChunkDatas;


import com.carpet.rof.extraWorldData.ExtraChunkData;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.singleTaskWorker.SPSCRingBuffer;
import com.carpet.rof.utils.singleTaskWorker.ROFTask;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ChunkModifyData extends ExtraChunkData
{


    public final Map<Long, ChunkData> chunkDataMap = new HashMap<>();
    public final SPSCRingBuffer<ChunkTaskData> chunkChangeBuffer;
    public long minChunkLifetime = 400;




    public static ChunkModifyData get(ServerWorld world)
    {
        return ExtraWorldDatas.fromWorld(world).chunkModifyData;
    }


    public ChunkModifyData(int bufferSize)
    {
        this.chunkChangeBuffer = new SPSCRingBuffer<>(bufferSize);
        this.needSave = true;
    }

    @Override
    public String getName()
    {
        return "chunkModifyData";
    }

    @Override
    public void write(NbtCompound nbt)
    {
        List<Long> chunks = new ArrayList<>();
        List<Long> chunkDatas = new ArrayList<>();
        for(Map.Entry<Long, ChunkData> entry : chunkDataMap.entrySet()){
            ChunkData chunkData = entry.getValue();
            if(chunkData.modifyTime - chunkData.createTime < minChunkLifetime){
                chunks.add(entry.getKey());
                chunkDatas.add(((chunkData.createTime)<<32L) | (int)(chunkData.modifyTime-chunkData.createTime));
            }
        }
        nbt.putLongArray("chunks",chunks.stream().mapToLong(Long::longValue).toArray());
        nbt.putLongArray("chunkDatas",chunkDatas.stream().mapToLong(Long::longValue).toArray());
    }

    @Override
    public void read(NbtCompound nbt)
    {
        chunkDataMap.clear();
        long[] chunksList = ROFWarp.getFromNbt(nbt.getLongArray("chunks"));
        long[] dataList = ROFWarp.getFromNbt(nbt.getLongArray("chunkDatas"));
        for(int i = 0; i < chunksList.length; i++){
            chunkDataMap.put(chunksList[i],new ChunkData(chunksList[i],(dataList[i]>>32),((int)dataList[i])+(dataList[i]>>32)));
        }
    }

    @Override
    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        write(nbt);
        return nbt;
    }

    public enum taskType
    {
        DELETE, CREATE, MODIFY
    }

    public class ChunkTaskData implements ROFTask
    {
        taskType type;
        long chunkPos;
        long time;

        ChunkTaskData(taskType type, long chunkPos, long time)
        {
            this.type = type;
            this.chunkPos = chunkPos;
            this.time = time;
        }

        @Override
        public void run()
        {
            if (type == taskType.DELETE) {
                ChunkModifyData.this.chunkDataMap.clear();
            } else if (type == taskType.CREATE) {
                if (!ChunkModifyData.this.chunkDataMap.containsKey(chunkPos)) {
                    ChunkModifyData.this.chunkDataMap.put(chunkPos, new ChunkData(chunkPos, time, time));
                   // rDEBUG("createChunk: " + new ChunkPos(chunkPos)+",size: "+ chunkDataMap.size());
                }
            } else if (type == taskType.MODIFY) {
                var chunk = ChunkModifyData.this.chunkDataMap.get(chunkPos);
                if (chunk != null) {
                    //rDEBUG("modifyChunk: " + new ChunkPos(chunkPos)+",size: "+ chunkDataMap.size());
                    chunk.modifyTime = time;
                    if(chunk.modifyTime - chunk.createTime > minChunkLifetime){
                        ChunkModifyData.this.chunkDataMap.remove(chunkPos);
                    }

                }
            }
        }
    }

    public static class ChunkData
    {

        public long chunkPos;
        public long createTime = -1;
        public long modifyTime = -1;

        ChunkData(long chunkPos, long createTime, long modifyTime)
        {
            this.chunkPos = chunkPos;
            this.createTime = createTime;
            this.modifyTime = modifyTime;
        }
    }

    public boolean createChunk(long chunkPos, long time)
    {
        return chunkChangeBuffer.offer(new ChunkTaskData(taskType.CREATE, chunkPos, time));
    }

    public boolean modifyChunk(long chunkPos, long time)
    {
        return chunkChangeBuffer.offer(new ChunkTaskData(taskType.MODIFY, chunkPos, time));
    }

    public boolean clear()
    {
        return chunkChangeBuffer.offer(new ChunkTaskData(taskType.DELETE, 0, 0));
    }

    public int getSize()
    {
        return  chunkDataMap.size();
    }
}
