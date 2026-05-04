package com.carpet.rof.mixin.packetRules;


import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.packerRules.PacketRulesSettings;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSeconds;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitTicks;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class EntityTrackerMixin
{
    @Mutable
    @Shadow @Final private int range;

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
           }
       }
        if(entitySpawnPacketLimitSeconds>=0) {
            var data2 = ExtraWorldDatas.fromWorld((ServerLevel) (ROFWarp.getWorld_(entity) )).chunkEntitySpawnLogger;
            data2.add(entity.chunkPosition().pack(), entity.getType());
            int count2 = data2.get(entity.chunkPosition().pack(), entity.getType());
            if (Math.random()*count2 >= entitySpawnPacketLimitSeconds) {
                this.range = 0;
            }
        }
    }

}
