package com.carpet.rof.mixin.packetRules;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.packerRules.PacketRulesSettings;
import com.carpet.rof.rules.packerRules.TrackedEntityRecoveryAccessor;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSeconds;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSecondsRecoverTime;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitTicks;
import com.carpet.rof.utils.ChunkPosHelper;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class EntityTrackerMixin implements TrackedEntityRecoveryAccessor
{
    @Mutable
    @Shadow @Final private int range;

    @Shadow @Final private Entity entity;

    @Shadow public abstract void updatePlayers(List<ServerPlayer> players);

    @Unique private boolean rof$spawnLimitedByTicks;

    @Unique private boolean rof$spawnLimitedBySeconds;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    void init(ChunkMap serverChunkLoadingManager, Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity, CallbackInfo ci){
       if(! (ROFWarp.getWorld_(entity) instanceof ServerLevel)) return ;
       if(entitySpawnPacketLimitTicks>=0 ) {

           var data = ExtraWorldDatas.fromWorld((ServerLevel) (ROFWarp.getWorld_(entity) )).entitySpawnCountsPerTick;
           if (data.containsKey(entity.getType())) {
               data.put(entity.getType(), data.get(entity.getType()) + 1);
           } else {
               data.put(entity.getType(), 1);
           }
           int count = data.get(entity.getType());
           if (count > entitySpawnPacketLimitTicks){
               this.range = PacketRulesSettings.entitySpawnPacketLimitTicksTrackerDistance;
               //ROFTool.rDEBUG("[EntityTrackerMixin] count: " + count);
               this.rof$spawnLimitedByTicks = true;
           }
       }
        if(entitySpawnPacketLimitSeconds>=0) {
            var data2 = ExtraWorldDatas.fromWorld((ServerLevel) (ROFWarp.getWorld_(entity) )).chunkEntitySpawnLogger;
            data2.add(ChunkPosHelper.pack(entity.chunkPosition()), entity.getType());
            int count2 = data2.get(ChunkPosHelper.pack(entity.chunkPosition()), entity.getType());
            if (Math.random()*count2 >= entitySpawnPacketLimitSeconds) {
                this.range = 0;
                this.rof$spawnLimitedBySeconds = true;
            }
        }
    }

    @Override
    public void rof$recoverSpawnLimit()
    {
        if (!rof$spawnLimitedBySeconds || rof$spawnLimitedByTicks) return;
        if (entity.tickCount < entitySpawnPacketLimitSecondsRecoverTime) return;
        rof$spawnLimitedBySeconds = false;
        this.range = entity.getType().clientTrackingRange() * 16;
        if (ROFWarp.getWorld_(entity) instanceof ServerLevel level) {
            updatePlayers(level.players());
        }
    }

}
