package com.carpet.rof.mixin.debug;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin
{
    // FULL 状态任务的入口：新生成或从磁盘读入的区块在这里被换成真正的 LevelChunk。
    @Inject(method = "full", at = @At(value = "HEAD"))
    private static void rof$logChunkFull(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks,
            ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir)
    {
        ServerLevel level = context.level();
        var pos = chunk.getPos();
        //ROFTool.rDEBUG("chunk full: " + level.dimensionTypeRegistration().getRegisteredName() + " " + pos);
    }
}
