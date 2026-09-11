package com.carpet.rof.rules.asyncWorldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

public final class LoadedBlockGetter implements BlockGetter
{
    private final ServerLevel level;
    private boolean available = true;
    private LevelChunk chunkCache = null;
    private long chunkPosCache = 0;

    public LoadedBlockGetter(ServerLevel level)
    {
        this.level = level;
    }

    public static ChunkAccess findChunk(ServerLevel level, int x, int z, ChunkStatus status)
    {
        ChunkHolder holder = level.getChunkSource().chunkMap.getVisibleChunkIfPresent(ChunkPos.pack(x, z));
        return holder == null ? null : holder.getChunkIfPresent(status);
    }


    public BlockEntity getBlockEntity(BlockPos pos)
    {
        LevelChunk chunk = chunk(pos);
        return chunk == null ? null : chunk.getBlockEntity(pos);
    }

    public @NonNull BlockState getBlockState(BlockPos pos)
    {
        LevelChunk chunk = chunk(pos);
        return chunk == null ? Blocks.VOID_AIR.defaultBlockState() : chunk.getBlockState(pos);
    }

    public @NonNull FluidState getFluidState(BlockPos pos)
    {
        LevelChunk chunk = chunk(pos);
        return chunk == null ? Fluids.EMPTY.defaultFluidState() : chunk.getFluidState(pos);
    }

    public int getHeight()
    {
        return level.getHeight();
    }

    public int getMinY()
    {
        return level.getMinY();
    }

    public boolean isAvailable()
    {
        return available;
    }

    private LevelChunk chunk(BlockPos pos)
    {
        if (pos.getY() < level.getMinY() || pos.getY() >= level.getMaxY())
            return null;
        if (ChunkPos.pack(pos) == chunkPosCache && chunkCache != null) {
            return chunkCache;
        }
        ChunkAccess chunk = findChunk(level, pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL);
        if (chunk instanceof LevelChunk levelChunk) {
            chunkCache = levelChunk;
            chunkPosCache = ChunkPos.pack(pos);
            return levelChunk;
        }
        ;
        available = false;
        return null;
    }
}
