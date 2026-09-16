package com.carpet.rof.mixin.rules.spawnStatistic;

import com.carpet.rof.rules.spawnStatistic.ChunkMapLevelAccess;
import com.carpet.rof.rules.spawnStatistic.LocalMobCapLevelAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalMobCapCalculator.class)
public abstract class LocalMobCapCalculatorMixin implements LocalMobCapLevelAccess
{
    @Shadow
    @Final
    private ChunkMap chunkMap;

    @Override
    public ServerLevel rof$localMobCapLevel()
    {
        return ((ChunkMapLevelAccess)this.chunkMap).rof$chunkMapLevel();
    }
}
