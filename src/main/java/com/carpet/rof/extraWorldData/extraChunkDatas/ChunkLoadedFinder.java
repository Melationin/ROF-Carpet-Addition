package com.carpet.rof.extraWorldData.extraChunkDatas;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.ChunkPos;

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
                x += it.x;
                z += it.z;
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
            set.add(c.toLong());
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

                retList.getLast().chunkConnected.add(new ChunkPos(pos));
                //set.remove(pos);
                int x = ChunkPos.getPackedX(pos);
                int z = ChunkPos.getPackedZ(pos);

                long[] neighbors = {
                        ChunkPos.toLong(x + 1, z),
                        ChunkPos.toLong(x - 1, z),
                        ChunkPos.toLong(x, z + 1),
                        ChunkPos.toLong(x, z - 1)
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
