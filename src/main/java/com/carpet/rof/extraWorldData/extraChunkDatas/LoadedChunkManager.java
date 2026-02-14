package com.carpet.rof.extraWorldData.extraChunkDatas;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class LoadedChunkManager
{
    public boolean needLog = false;
    public Set<ChunkPos>  loadedChunks = new HashSet<ChunkPos>();

    public static class ConnectedChunksData{
        final Set<ChunkPos> loadedChunksConnected = new HashSet<>();
        public int size(){
            return loadedChunksConnected.size();
        }

        public ChunkPos getCenterChunk(){
            double x = 0;
            double z = 0;

            for(var it : loadedChunksConnected){
                x += it.x;
                z += it.z;
            }
            x/=loadedChunksConnected.size();
            z/=loadedChunksConnected.size();

            return new ChunkPos((int)x,(int)z);
        }
    }


    public List<ConnectedChunksData> getConnectedChunks()
    {
        LongOpenHashSet  set = new LongOpenHashSet();
        List<ConnectedChunksData> retList = new ArrayList<ConnectedChunksData>();
        for(ChunkPos c : this.loadedChunks){
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

                retList.getLast().loadedChunksConnected.add(new ChunkPos(pos));
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
