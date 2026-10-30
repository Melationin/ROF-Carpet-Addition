package com.carpet.rof.mixin.entity.enderPearl;

import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin
{
    @Shadow @Final private ServerLevel level;

    @ModifyVariable(method = "onFullChunkStatusChange", at = @At("HEAD"), argsOnly = true)
    private FullChunkStatus rof$keepPearlEntitiesTicking(FullChunkStatus status, ChunkPos pos, FullChunkStatus originalStatus)
    {
        return ExtraWorldDatas.fromWorld(level).enderPearlTickets.onLoadingStatusChange(level, pos, status);
    }
}
