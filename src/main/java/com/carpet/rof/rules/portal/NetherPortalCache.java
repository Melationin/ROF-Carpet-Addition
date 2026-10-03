package com.carpet.rof.rules.portal;

import com.carpet.rof.mixinAccessor.LevelChunkAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;

public class NetherPortalCache
{
    private final Long2ObjectLinkedOpenHashMap<Entry> entries = new Long2ObjectLinkedOpenHashMap<>();

    public BlockPos get(BlockPos entrance, ServerLevel targetLevel, Vec3 position, double distance, WorldBorder border)
    {
        long key = entrance.asLong();
        Entry entry = this.entries.get(key);
        if (entry == null) return null;
        if (entry.targetLevel != targetLevel
                || !(entry.searchPosition.distanceToSqr(position) <= distance * distance)) {
            this.entries.remove(key);
            return null;
        }
        BlockPos exit = entry.exit;
        LevelChunk chunk = targetLevel.getChunkSource().getChunkNow(exit.getX() >> 4, exit.getZ() >> 4);
        if (chunk == null
                || entry.chunk.get() != chunk
                || entry.blockChangeStamp != LevelChunkAccessor.of(chunk).rof$getBlockChangeStamp()
                || !border.isWithinBounds(exit)
                || !chunk.getBlockState(exit).is(Blocks.NETHER_PORTAL)) {
            this.entries.remove(key);
            return null;
        }
        this.entries.getAndMoveToLast(key);
        return exit;
    }

    public void put(BlockPos entrance, ServerLevel targetLevel, Vec3 searchPosition, BlockPos exit)
    {
        LevelChunk chunk = targetLevel.getChunkSource().getChunkNow(exit.getX() >> 4, exit.getZ() >> 4);
        if (chunk == null) return;
        this.entries.putAndMoveToLast(
                entrance.asLong(),
                new Entry(
                        targetLevel,
                        searchPosition,
                        exit.immutable(),
                new WeakReference<>(chunk),
                        LevelChunkAccessor.of(chunk).rof$getBlockChangeStamp()
                ));
        if (this.entries.size() > 1024) this.entries.removeFirst();
    }

    public void clear()
    {
        this.entries.clear();
    }

    private record Entry(ServerLevel targetLevel, Vec3 searchPosition, BlockPos exit,
                         WeakReference<LevelChunk> chunk, int blockChangeStamp)
    {
    }
}
