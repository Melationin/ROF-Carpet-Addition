package com.carpet.rof.mixin.packetRules;


import com.carpet.rof.commands.SearchCommand;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.packerRules.PacketRulesSettings;
import com.carpet.rof.utils.RofTool;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSeconds;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitTicks;

@Mixin(ServerChunkLoadingManager.EntityTracker.class)
public class EntityTrackerMixin
{
    @Mutable
    @Shadow @Final private int maxDistance;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    void init(ServerChunkLoadingManager serverChunkLoadingManager, Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity, CallbackInfo ci){
       if(! (RofTool.getWorld_(entity) instanceof ServerWorld)) return ;
       if(entitySpawnPacketLimitTicks>=0 ) {

           var data = ExtraWorldDatas.fromWorld((ServerWorld) (RofTool.getWorld_(entity) )).entitySpawnCountsPerTick;
           if (data.containsKey(entity.getType())) {
               data.put(entity.getType(), data.get(entity.getType()) + 1);
           } else {
               data.put(entity.getType(), 1);
           }
           int count = data.get(entity.getType());
           if (count > entitySpawnPacketLimitTicks){
               this.maxDistance = PacketRulesSettings.limitedEntityTrackerDistance;
           }
       }
        if(entitySpawnPacketLimitSeconds>=0) {

            var data2 = ExtraWorldDatas.fromWorld((ServerWorld) (RofTool.getWorld_(entity) )).chunkEntitySpawnLogger;
            data2.add(entity.getChunkPos().toLong(), entity.getType());
            int count2 = data2.get(entity.getChunkPos().toLong(), entity.getType());
            if (count2 > entitySpawnPacketLimitSeconds) {
                this.maxDistance = 0;
            }
        }
    }
}
