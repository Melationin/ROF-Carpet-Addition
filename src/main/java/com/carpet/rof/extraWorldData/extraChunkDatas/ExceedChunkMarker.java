package com.carpet.rof.extraWorldData.extraChunkDatas;


import com.carpet.rof.extraWorldData.ExtraChunkData;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.RofTool;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import static com.carpet.rof.utils.RofTool.rDEBUG;


public class ExceedChunkMarker extends ExtraChunkData
{
    public int topY;
    private final Long2IntMap chunkHighestBlockPosMap = new Long2IntOpenHashMap();
    protected final LongOpenHashSet chunks = new LongOpenHashSet();
    private LongOpenHashSet tempChunks = new LongOpenHashSet();


    private final String name = "exceedChunkMarker";

    public Thread workerThread;

    @Override
    public String getName()
    {
        return name;
    }

    public ExceedChunkMarker(int topY)
    {
        this.topY = topY;
        this.needSave = true;
    }

    private long chunkCache;
    private boolean chunkCacheValue;


    public void clear() {
      chunks.clear();
      chunkCacheValue = false;
      chunkCache = 0;
      chunkHighestBlockPosMap.clear();
    }

    public boolean isNotHighChunk(int x, int z) {
        long hash = ChunkPos.toLong(x,z);
        if(hash==chunkCache){
            return chunkCacheValue;
        }
        if(chunks.contains(hash)){
            chunkCacheValue = false;
            chunkCache = hash;
            return false;

        }else {
            chunkCacheValue = true;
            chunkCache = hash;
            return true;
        }
    }

    public void addChunk(long chunk)
    {
        if(chunk == chunkCache){
            chunkCacheValue = false;
        }
        rDEBUG("addChunk: " + RofTool.getChunkPos(chunk));
        chunks.add(chunk);
    }

    public void removeChunk(long chunk)
    {
        if(chunk == chunkCache){
            chunkCacheValue = true;
        }
        rDEBUG("removeChunk: " + RofTool.getChunkPos(chunk));
        chunks.remove(chunk);
    }


    public boolean isMustAir(int x, int y,int z)
    {
        return y >= topY
                && isNotHighChunk(x>>4,z>>4);
    }

    public static boolean isMustAir(ServerWorld world, BlockPos pos)
    {
        ExceedChunkMarker exceedChunkMarker = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        return exceedChunkMarker.isMustAir(pos.getX(),pos.getY(),pos.getZ());
    }

    private  long getHighest(long data){
        long Res = -1000;
        for(int i = 0; i<64 - 9; i+= 9) {
            long s1  = data&((1L << 9)- 1);
            if(s1>Res) Res = s1;
            data >>= 9;
        }
        return Res;
    }

    public int getSize(){
        return chunks.size();
    }

    public void update(ServerWorld  world) {

        if(!tempChunks.isEmpty()){
            chunks.addAll(tempChunks);
            tempChunks.clear();
        }

        outerLoop:
        for(long l : chunks) {
            if((world.getTime()+l)%400 == 0) {
                ChunkPos chunkPos = new ChunkPos(l);
                Chunk chunk = world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
                if(chunk != null) {
                    Heightmap hmp =  chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
                    int maxPos = chunkHighestBlockPosMap.getOrDefault(chunkPos.toLong(),0);
                    if(maxPos!=0 && (hmp.get(maxPos/16,maxPos%16) > topY)) continue;
                    for(int i = 0;i<256;++i)
                        if(hmp.get(i/16,i%16) > topY){
                            chunkHighestBlockPosMap.put(l,i);
                            continue outerLoop;
                        }
                    removeChunk(l);
                }
            }
        }
    }

    @Override
    public void write(NbtCompound nbtCompound){
        nbtCompound.putLongArray("chunks", chunks.toLongArray());
        nbtCompound.putInt("topY", topY);
    }

    @Override
    public void read(NbtCompound nbt)
    {
        //? >1.21.4 {
        for(long chunk : nbt.getLongArray("chunks").get()){
            chunks.add(chunk);
        }
        topY = nbt.getInt("topY").get();
        //?} else {
        /*for(long chunk : nbt.getLongArray("chunks")){
            chunks.add(chunk);
        }
        topY = nbt.getInt("topY");

        *///?}
    }

    @Override
    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        write(nbt);
        return nbt;
    }




    public void loadFromWorld(ServerWorld world, AtomicDouble process){
        tempChunks = new LongOpenHashSet();
        workerThread = new Thread(()->{
            ROFIO.forEachExistingChunk(world,chunkData->{
                        //? if >1.21.4 {
                        int finalJ = chunkData.getInt("xPos").get();
                        int finalI = chunkData.getInt("zPos").get();
                        chunkData.getCompound("Heightmaps").flatMap(heightmaps -> heightmaps.getLongArray(Heightmap.Type.MOTION_BLOCKING.getId())).ifPresent(heightmap -> {
                            for (long l : heightmap) {
                                if (getHighest(l) + world.getBottomY() > topY) {
                                    tempChunks.add(ChunkPos.toLong(finalJ, finalI));
                                    break;
                                }
                            }
                        });
                        //?} else {
                        /*int finalJ = chunkData.getInt("xPos");
                        int finalI = chunkData.getInt("zPos");
                        for (long l :  chunkData.getCompound("Heightmaps").getLongArray(Heightmap.Type.MOTION_BLOCKING.getName())) {
                                if (getHighest(l) + world.getBottomY() > topY) {
                                    tempChunks.add(ChunkPos.toLong(finalJ, finalI));
                                    break;
                                }
                        }
                        *///?}
                    }
                    ,process);
        });
        workerThread.start();
    }


}
