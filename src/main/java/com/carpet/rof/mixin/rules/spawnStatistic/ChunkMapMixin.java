package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.rules.spawnStatistic.ChunkMapLevelAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements ChunkMapLevelAccess
{
    @Shadow
    @Final
    private ServerLevel level;

    @Override
    public ServerLevel rof$chunkMapLevel()
    {
        return this.level;
    }
}
