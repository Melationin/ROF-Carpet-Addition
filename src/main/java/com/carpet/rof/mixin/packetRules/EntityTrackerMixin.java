package com.carpet.rof.mixin.packetRules;


import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.mixinAccessor.ChunkMapRecoveryAccessor;
import com.carpet.rof.mixinAccessor.TrackedEntityRecoveryAccessor;
import com.carpet.rof.utils.ChunkPosHelper;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? >=26.3
//import net.minecraft.world.entity.UpdateInterval;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.carpet.rof.packetRules.PacketRulesSettings.*;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class EntityTrackerMixin implements TrackedEntityRecoveryAccessor
{
    @Mutable
    @Shadow @Final private int range;

    @Shadow @Final private Entity entity;

    @Shadow public abstract void updatePlayers(List<ServerPlayer> players);

    @Unique private boolean rof$spawnLimited;
    @Unique private int rof$originalRange;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    //?>=26.3
    //void init(ChunkMap serverChunkLoadingManager, Entity entity, int maxDistance, UpdateInterval tickInterval, boolean alwaysUpdateVelocity, CallbackInfo ci){
    //?<26.3
    void init(ChunkMap serverChunkLoadingManager, Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity, CallbackInfo ci){
        if ((entitySpawnPacketLimitTicks < 0 && entitySpawnPacketLimitSeconds < 0)
                || !(ROFWarp.getWorld_(entity) instanceof ServerLevel level)) return;
        rof$originalRange = range;
        var data = ExtraWorldDatas.fromWorld(level);
        if (entitySpawnPacketLimitTicks >= 0) {
            int count = data.entitySpawnCountsPerTick.merge(entity.getType(), 1, Integer::sum);
            if (count > entitySpawnPacketLimitTicks) {
                this.range = entitySpawnPacketLimitTicksTrackerDistance;
                rof$spawnLimited = true;
            }
        }
        if (entitySpawnPacketLimitSeconds >= 0) {
            long chunk = ChunkPosHelper.pack(entity.chunkPosition());
            data.chunkEntitySpawnLogger.add(chunk, entity.getType());
            int count = data.chunkEntitySpawnLogger.get(chunk, entity.getType());
            if (Math.random() * count >= entitySpawnPacketLimitSeconds) {
                this.range = 0;
                rof$spawnLimited = true;
            }
        }
        if (rof$spawnLimited) ((ChunkMapRecoveryAccessor) serverChunkLoadingManager).rof$markSpawnLimitedEntity();
    }

    @Override
    public boolean rof$recoverSpawnLimit()
    {
        if (!rof$spawnLimited) return false;
        if (entitySpawnPacketLimitSecondsRecoverTime < 0 || entity.tickCount < entitySpawnPacketLimitSecondsRecoverTime) return true;
        rof$spawnLimited = false;
        this.range = rof$originalRange;
        if (ROFWarp.getWorld_(entity) instanceof ServerLevel level) {
            updatePlayers(level.players());
        }
        return false;
    }

}
