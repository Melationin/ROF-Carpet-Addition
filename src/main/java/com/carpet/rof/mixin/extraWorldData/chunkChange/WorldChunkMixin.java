package com.carpet.rof.mixin.extraWorldData.chunkChange;

import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkModifyData;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin
{

    @Shadow @Final private World world;

    @Shadow public abstract ChunkStatus getStatus();


    //? >1.21.4 {
    @Inject(method = "setBlockState",
            at = @At(value = "HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir)
    {
        if(world instanceof ServerWorld serverworld
        ) {
            ChunkModifyData.get(serverworld).modifyChunk(((Chunk)(Object)this).getPos().toLong(),world.getTime());
        }
    }

    //?} else {
    /*@Inject(method = "setBlockState",
            at = @At(value = "HEAD"))
    public void setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir)
    {
        if(world instanceof ServerWorld serverworld
        ) {
            ChunkModifyData.get(serverworld).modifyChunk(((Chunk)(Object)this).getPos().toLong(),world.getTime());
        }
    }
    *///?}
}
