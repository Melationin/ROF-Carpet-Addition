package com.carpet.rof.world.extraWorldData.extraChunkDatas;

import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFIO;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.world.extraWorldData.ExtraChunkData;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.carpet.rof.utils.ROFTool.rDEBUG;

public class ExceedChunkMarker extends ExtraChunkData
{
    public int topY;
    private int maxBuildY = Integer.MAX_VALUE / 2;

    // 仅保存最高高度（Heightmap.getFirstAvailable，即最高非空气方块 Y + 1）> topY 的区块
    protected final Long2IntOpenHashMap chunks = new Long2IntOpenHashMap();
    // 缓存已加载超限区块取得最高高度的列下标 (localX << 4 | localZ)，用于 O(1) 校验峰值是否未降
    private final Long2IntMap chunkHighestBlockPosMap = new Long2IntOpenHashMap();
    private volatile Long2IntOpenHashMap tempChunks;

    private final String name = "exceedChunkMarker";

    public Thread workerThread;

    private long chunkCache;
    private int chunkCacheTopY;
    private boolean chunkCacheValid;

    public ExceedChunkMarker(int topY)
    {
        this.topY = topY;
        this.needSave = true;
        this.chunkHighestBlockPosMap.defaultReturnValue(-1);
    }

    public void initWorld(ServerLevel world)
    {
        this.maxBuildY = world.getMaxY() + 1;
        this.topY = ROFTool.isNetherWorld(world) ? 128 : Integer.MAX_VALUE / 2;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void clear()
    {
        this.tempChunks = null;
        this.chunks.clear();
        this.chunkHighestBlockPosMap.clear();
        this.invalidateCache();
    }

    public void setTopY(int topY)
    {
        this.topY = topY;
        this.flushTempChunks();
        this.chunks.long2IntEntrySet().removeIf(entry -> {
            if (entry.getIntValue() <= topY)
            {
                this.chunkHighestBlockPosMap.remove(entry.getLongKey());
                return true;
            }
            return false;
        });
        this.invalidateCache();
    }

    private void invalidateCache()
    {
        this.chunkCacheValid = false;
        this.chunkCache = 0L;
        this.chunkCacheTopY = this.topY;
    }

    private void flushTempChunks()
    {
        Long2IntOpenHashMap pending = this.tempChunks;
        if (pending != null)
        {
            this.tempChunks = null;
            for (Long2IntMap.Entry entry : pending.long2IntEntrySet())
            {
                if (entry.getIntValue() > this.topY)
                {
                    this.chunks.put(entry.getLongKey(), entry.getIntValue());
                }
            }
            this.invalidateCache();
        }
    }

    public int getChunkTopY(int chunkX, int chunkZ)
    {
        long hash = ChunkPosHelper.pack(chunkX, chunkZ);
        if (this.chunkCacheValid && hash == this.chunkCache)
        {
            return this.chunkCacheTopY;
        }
        int maxY = this.chunks.getOrDefault(hash, this.topY);
        if (maxY <= this.topY)
        {
            maxY = this.topY;
        }
        this.chunkCache = hash;
        this.chunkCacheTopY = maxY;
        this.chunkCacheValid = true;
        return maxY;
    }

    public boolean isNotHighChunk(int x, int z)
    {
        return this.getChunkTopY(x, z) <= this.topY;
    }

    public void onHeightChanged(long chunk, int localX, int localZ, int height)
    {
        if (height <= this.topY) return;
        int currentMax = this.chunks.getOrDefault(chunk, this.topY);
        if (height > currentMax)
        {
            this.putChunk(chunk, height);
            this.chunkHighestBlockPosMap.put(chunk, (localX << 4) | (localZ & 15));
        }
    }

    public void putChunk(long chunk, int maxY)
    {
        if (maxY <= this.topY) return;
        if (this.chunkCacheValid && chunk == this.chunkCache)
        {
            this.chunkCacheTopY = maxY;
        }
        rDEBUG("putChunk: " + ROFTool.getChunkPos(chunk) + " maxY=" + maxY);
        this.chunks.put(chunk, maxY);
    }

    public void removeChunk(long chunk, ObjectIterator<Long2IntMap.Entry> it)
    {
        if (this.chunkCacheValid && chunk == this.chunkCache)
        {
            this.chunkCacheTopY = this.topY;
        }
        this.chunkHighestBlockPosMap.remove(chunk);
        rDEBUG("removeChunk: " + ROFTool.getChunkPos(chunk));
        it.remove();
    }

    public boolean mustBeAir(int x, int y, int z)
    {
        return y >= this.topY && y >= this.getChunkTopY(x >> 4, z >> 4);
    }

    public static boolean mustBeAir(ServerLevel world, BlockPos pos)
    {
        if (world.isOutsideBuildHeight(pos)) return true;
        ExceedChunkMarker exceedChunkMarker = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        return exceedChunkMarker.mustBeAir(pos.getX(), pos.getY(), pos.getZ());
    }

    private long getHighest(long data)
    {
        long res = -1000;
        for (int i = 0; i < 64 - 9; i += 9)
        {
            long s1 = data & ((1L << 9) - 1);
            if (s1 > res) res = s1;
            data >>= 9;
        }
        return res;
    }

    public int getSize()
    {
        this.flushTempChunks();
        return this.chunks.size();
    }

    public void update(ServerLevel world)
    {
        this.flushTempChunks();

        var it = this.chunks.long2IntEntrySet().fastIterator();
        while (it.hasNext())
        {
            Long2IntMap.Entry entry = it.next();
            long l = entry.getLongKey();
            if ((world.getGameTime() + l) % 400 != 0) continue;

            ChunkPos chunkPos = ChunkPosHelper.unpack(l);
            ChunkAccess chunk = world.getChunkSource().getChunk(ChunkPosHelper.x(chunkPos), ChunkPosHelper.z(chunkPos), ChunkStatus.FULL, false);
            if (chunk == null) continue;

            Heightmap hmp = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE);
            int storedMax = entry.getIntValue();
            int maxPos = this.chunkHighestBlockPosMap.getOrDefault(l, -1);
            if (maxPos >= 0 && storedMax > this.topY && hmp.getFirstAvailable(maxPos >> 4, maxPos & 15) == storedMax)
            {
                continue;
            }

            int actualMax = Integer.MIN_VALUE;
            int bestIndex = 0;
            for (int i = 0; i < 256; ++i)
            {
                int h = hmp.getFirstAvailable(i >> 4, i & 15);
                if (h > actualMax)
                {
                    actualMax = h;
                    bestIndex = i;
                }
            }

            if (actualMax > this.topY)
            {
                entry.setValue(actualMax);
                this.chunkHighestBlockPosMap.put(l, bestIndex);
                if (this.chunkCacheValid && l == this.chunkCache)
                {
                    this.chunkCacheTopY = actualMax;
                }
            }
            else
            {
                this.removeChunk(l, it);
            }
        }
    }

    @Override
    public void write(CompoundTag nbtCompound)
    {
        this.flushTempChunks();
        int size = this.chunks.size();
        long[] chunkKeys = new long[size];
        int[] heights = new int[size];
        int index = 0;
        for (Long2IntMap.Entry entry : this.chunks.long2IntEntrySet())
        {
            int maxY = entry.getIntValue();
            if (maxY > this.topY)
            {
                chunkKeys[index] = entry.getLongKey();
                heights[index] = maxY;
                index++;
            }
        }
        if (index != size)
        {
            chunkKeys = Arrays.copyOf(chunkKeys, index);
            heights = Arrays.copyOf(heights, index);
        }
        nbtCompound.putLongArray("chunks", chunkKeys);
        nbtCompound.putIntArray("heights", heights);
        nbtCompound.putInt("topY", this.topY);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.clear();
        long[] chunkKeys;
        int[] heights;
        //? >1.21.4 {
        this.topY = nbt.getInt("topY").orElse(Integer.MAX_VALUE - 1);
        chunkKeys = nbt.getLongArray("chunks").orElse(new long[0]);
        heights = nbt.getIntArray("heights").orElse(new int[0]);
        //?} else {
        /*this.topY = nbt.getInt("topY");
        chunkKeys = nbt.getLongArray("chunks");
        heights = nbt.getIntArray("heights");
        *///?}

        // 旧版本仅保存了 chunks 数组而没有 heights，直接将最高高度记为区块上限
        boolean hasHeights = heights.length == chunkKeys.length;
        for (int i = 0; i < chunkKeys.length; i++)
        {
            int maxY = hasHeights ? heights[i] : this.maxBuildY;
            if (maxY > this.topY)
            {
                this.chunks.put(chunkKeys[i], maxY);
            }
        }
    }

    @Override
    public CompoundTag toNbt()
    {
        CompoundTag nbt = new CompoundTag();
        this.write(nbt);
        return nbt;
    }

    public void loadFromWorld(ServerLevel world, AtomicDouble process)
    {
        this.maxBuildY = world.getMaxY() + 1;
        int threshold = this.topY;
        Long2IntOpenHashMap loadedChunks = new Long2IntOpenHashMap();
        this.workerThread = new Thread(() -> {
            var future = ROFIO.forEachExistingChunkParallel(world, chunkData -> {
                AtomicInteger maxChunkHeight = new AtomicInteger(Integer.MIN_VALUE);
                //? if >1.21.4 {
                chunkData.getCompound("Heightmaps").flatMap(heightmaps -> heightmaps.getLongArray(
                        Heightmap.Types.WORLD_SURFACE.getSerializationKey())).ifPresent(heightmap -> {
                    int max = Integer.MIN_VALUE;
                    for (long l : heightmap)
                    {
                        int h = (int) this.getHighest(l) + world.getMinY();
                        if (h > max) max = h;
                    }
                    maxChunkHeight.set(max);
                });
                //?} else {
                /*int max = Integer.MIN_VALUE;
                for (long l : chunkData.getCompound("Heightmaps").getLongArray(Heightmap.Type.WORLD_SURFACE.getName()))
                {
                    int h = (int) this.getHighest(l) + world.getBottomY();
                    if (h > max) max = h;
                }
                maxChunkHeight.set(max);
                *///?}
                return maxChunkHeight.get();
            }, process);

            for (var entry : future.join().entrySet())
            {
                int maxY = entry.getValue();
                if (maxY > threshold)
                {
                    loadedChunks.put(ChunkPosHelper.pack(entry.getKey()), maxY);
                }
            }
            this.tempChunks = loadedChunks;
            process.set(1);
        });

        this.workerThread.start();
    }
}
