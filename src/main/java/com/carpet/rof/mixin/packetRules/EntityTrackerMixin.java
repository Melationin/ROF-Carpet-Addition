package com.carpet.rof.mixin.packetRules;

import com.carpet.rof.rules.entityPacketLimit.EntityPacketLimit;
import com.carpet.rof.rules.entityPacketLimit.EntityPacketLimitSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.carpet.rof.rules.entityPacketLimit.EntityPacketLimitSetting.entityPacketLimit;

@Mixin(value = ServerChunkLoadingManager.EntityTracker.class,priority = 1001)
public class EntityTrackerMixin {



    @Unique
    public boolean shouldBeLimited(ServerPlayerEntity player, Packet<?> packet) {
        if(packet instanceof EntityS2CPacket EntityS2CPacket) {
            if( EntityS2CPacket.getEntity(player.getEntityWorld()) instanceof ServerPlayerEntity){
                return false;
            }else {
                Integer i = EntityPacketLimit.entityPacketCountPerTick.getOrDefault(player.getUuid(),0);
                EntityPacketLimit.entityPacketCountPerTick.put(player.getUuid(),i+1);
                if(i+1<= entityPacketLimit){
                    return false;
                }else {
                    return true;
                }
            }
        }

        if(packet instanceof EntityAnimationS2CPacket || packet instanceof EntityAttributesS2CPacket) {
            Integer i = EntityPacketLimit.entityPacketCountPerTick.getOrDefault(player.getUuid(),0);
            EntityPacketLimit.entityPacketCountPerTick.put(player.getUuid(),i+1);
            if(i+1<= entityPacketLimit){
                return false;
            }else {
                return true;
            }
        }
        return false;
    }


    @Redirect(method = "sendToListeners",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/PlayerAssociatedNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")
    )
    public void s(PlayerAssociatedNetworkHandler instance, Packet<?> packet){
        if(entityPacketLimit<=0){
            instance.sendPacket(packet);
            return;
        }
       if(!shouldBeLimited(instance.getPlayer(),packet)){
           instance.sendPacket(packet);
       }
    }

    @Redirect(method = "sendToListenersIf",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/PlayerAssociatedNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    public  void a(PlayerAssociatedNetworkHandler instance, Packet<?> packet){
        if(entityPacketLimit<=0){
            instance.sendPacket(packet);
            return;
        }
        if(packet instanceof EntityS2CPacket entityPacket){
            if(entityPacket.getEntity(instance.getPlayer().getEntityWorld()) instanceof ServerPlayerEntity){
                instance.sendPacket(entityPacket);
            }else {
                Integer i = EntityPacketLimit.entityPacketCountPerTick.getOrDefault(instance.getPlayer().getUuid(),0);
                EntityPacketLimit.entityPacketCountPerTick.put(instance.getPlayer().getUuid(),i+1);
                if(i+1<= entityPacketLimit){
                    instance.sendPacket(entityPacket);
                }
            }
        }else {
            instance.sendPacket(packet);
        }

    }

}

