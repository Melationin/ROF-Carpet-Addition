package com.carpet.rof.extraWorldData.extraChunkDatas;


import com.carpet.rof.extraWorldData.ExtraChunkData;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.ROFTool;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.carpet.rof.utils.ROFTool.rDEBUG;


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
        long hash = ChunkPos.pack(x,z);
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
        rDEBUG("addChunk: " + ROFTool.getChunkPos(chunk));
        chunks.add(chunk);
    }

    public void removeChunk(long chunk, LongIterator it)
    {
        if(chunk == chunkCache){
            chunkCacheValue = true;
        }
        rDEBUG("removeChunk: " + ROFTool.getChunkPos(chunk));
        it.remove();
    }


    public boolean mustBeAir(int x, int y, int z)
    {
        return y >= topY
                && isNotHighChunk(x>>4,z>>4);
    }

    public static boolean mustBeAir(ServerLevel world, BlockPos pos)
    {
        if(world.isOutsideBuildHeight(pos)) return true;
        ExceedChunkMarker exceedChunkMarker = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        return exceedChunkMarker.mustBeAir(pos.getX(),pos.getY(),pos.getZ());
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

    public void update(ServerLevel world) {

        if(!tempChunks.isEmpty()){
            chunks.addAll(tempChunks);
            tempChunks.clear();
        }

        var it = chunks.iterator();

        outerLoop:
        while (it.hasNext()){
            long l = it.nextLong();
            if((world.getGameTime()+l)%400 == 0) {
                ChunkPos chunkPos = ChunkPos.unpack(l);
                ChunkAccess chunk = world.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, false);
                if(chunk != null) {
                    Heightmap hmp =  chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
                    int maxPos = chunkHighestBlockPosMap.getOrDefault(chunkPos.pack(),0);
                    if(maxPos!=0 && (hmp.getFirstAvailable(maxPos/16,maxPos%16) > topY)) continue;
                    for(int i = 0;i<256;++i)
                        if(hmp.getFirstAvailable(i/16,i%16) > topY){
                            chunkHighestBlockPosMap.put(l,i);
                            continue outerLoop;
                        }
                    removeChunk(l,it);
                }
            }
        }

    }

    @Override
    public void write(CompoundTag nbtCompound){
        nbtCompound.putLongArray("chunks", chunks.toLongArray());
        nbtCompound.putInt("topY", topY);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        //? >1.21.4 {
        for(long chunk : nbt.getLongArray("chunks").orElse(new long[0])){
            chunks.add(chunk);
        }
        topY = nbt.getInt("topY").orElse(Integer.MAX_VALUE - 1);
        //?} else {
        /*for(long chunk : nbt.getLongArray("chunks")){
            chunks.add(chunk);
        }
        topY = nbt.getInt("topY");

        *///?}
    }

    @Override
    public CompoundTag toNbt()
    {
        CompoundTag nbt = new CompoundTag();
        write(nbt);
        return nbt;
    }




    public void loadFromWorld(ServerLevel world, AtomicDouble process){

        LongOpenHashSet tempChunks2 = new LongOpenHashSet();
        workerThread = new Thread(()->{
                   var future = ROFIO.forEachExistingChunkParallel(world,chunkData->{


                       AtomicBoolean isHighChunk = new AtomicBoolean(false);
                        //? if >1.21.4 {

                        chunkData.getCompound("Heightmaps").flatMap(heightmaps -> heightmaps.getLongArray(
                                Heightmap.Types.MOTION_BLOCKING.getSerializationKey())).ifPresent(heightmap -> {
                            for (long l : heightmap) {
                                if (getHighest(l) + world.getMinY() > topY) {
                                    isHighChunk.set(true);
                                    break;
                                }
                            }
                        });

                        //?} else {
                        /*
                        for (long l :  chunkData.getCompound("Heightmaps").getLongArray(Heightmap.Type.MOTION_BLOCKING.getName())) {
                                if (getHighest(l) + world.getBottomY() > topY) {
                                    isHighChunk.set(true);
                                    break;
                                }
                        }
                        *///?}

                        return isHighChunk.get();
                    }
                    ,process);
                   for(var entry: future.join().entrySet()){
                       if(entry.getValue() == true){
                           tempChunks2.add(entry.getKey().pack());
                       }
                   }
                   tempChunks = tempChunks2;
                   process.set(1);
        });


        workerThread.start();
    }


}
