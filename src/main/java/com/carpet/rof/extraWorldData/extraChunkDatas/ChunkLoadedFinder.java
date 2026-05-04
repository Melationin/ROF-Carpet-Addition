package com.carpet.rof.extraWorldData.extraChunkDatas;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class ChunkLoadedFinder
{
    public boolean needLog = false;
    public Set<ChunkPos> ChunkLoadedMap = new HashSet<>();

    public static class ConnectedChunksData{
        final Set<ChunkPos> chunkConnected = new HashSet<>();
        public int size(){
            return chunkConnected.size();
        }

        public ChunkPos getCenterChunk(){
            double x = 0;
            double z = 0;

            for(var it : chunkConnected){
                x += it.x();
                z += it.z();
            }
            x/= chunkConnected.size();
            z/= chunkConnected.size();

            return new ChunkPos((int)x,(int)z);
        }
    }


    public List<ConnectedChunksData> getConnectedChunks()
    {
        LongOpenHashSet  set = new LongOpenHashSet();
        List<ConnectedChunksData> retList = new ArrayList<>();
        for(ChunkPos c : this.ChunkLoadedMap){
            set.add(c.pack());
        }
        LongArrayList stack = new LongArrayList();
        while(!set.isEmpty()){
            if(stack.isEmpty()){
                var it = set.longIterator();
                retList.add(new ConnectedChunksData());
                stack.add(it.nextLong());
                it.remove();
            }
            while(!stack.isEmpty()){
                long pos = stack.popLong();

                retList.getLast().chunkConnected.add(ChunkPos.unpack(pos));
                //set.remove(pos);
                int x = ChunkPos.getX(pos);
                int z = ChunkPos.getZ(pos);

                long[] neighbors = {
                        ChunkPos.pack(x + 1, z),
                        ChunkPos.pack(x - 1, z),
                        ChunkPos.pack(x, z + 1),
                        ChunkPos.pack(x, z - 1)
                };
                for(var i :neighbors){
                    if(set.remove(i)){
                        //LogUtils.getLogger().info(String.format("Found chunk at %s", new ChunkPos(i).toString()));
                        stack.add(i);
                    }
                }
            }
        }
        return retList;
    }


}
