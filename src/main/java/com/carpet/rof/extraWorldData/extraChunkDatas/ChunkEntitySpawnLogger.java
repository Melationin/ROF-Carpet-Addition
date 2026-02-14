package com.carpet.rof.extraWorldData.extraChunkDatas;

import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChunkEntitySpawnLogger
{

    public record Key (
        long chunkPos,
        EntityType<?> entityType
    )
    {
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return ((Key)o).chunkPos == chunkPos && ((Key)o).entityType == entityType;
        }

        public int hashCode(){
            return Objects.hash(chunkPos, entityType);
        }
    }

    //每秒更新一次
    final Map<Key,Integer> entitySpawnChunkCount = new HashMap<>();
    final Map<Key,Integer> entitySpawnChunkCountLast = new HashMap<>();

    public void run(ServerWorld world){
        if(world.getTime()%20 == 0){
            entitySpawnChunkCountLast.clear();
            entitySpawnChunkCountLast.putAll(entitySpawnChunkCount);
            entitySpawnChunkCount.clear();
        }
    }

    public void add(long pos, EntityType<?> entityType)
    {
        entitySpawnChunkCount.merge(new Key(pos,entityType),1,Integer::sum);
    }

    public int get(long pos, EntityType<?> entityType){
        return entitySpawnChunkCountLast.getOrDefault(new Key(pos,entityType),0);
    }


}

