//package com.carpet.rof.mixin.network;
//
//import com.carpet.rof.utils.PacketRecord;
//import net.minecraft.entity.Entity;
//import net.minecraft.network.packet.Packet;
//import net.minecraft.network.packet.PacketType;
//import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
//import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
//import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
//import net.minecraft.server.world.ServerChunkLoadingManager;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.List;
//import java.util.UUID;
//
//@Mixin(ServerChunkLoadingManager.EntityTracker.class)
//public abstract class ServerChunkLoadingManager$EntityTrackerMixin {
//    /*
//    @Inject(method = "sendToNearbyPlayers",at = @At(value = "HEAD"))
//    private void sendToNearbyPlayers(Packet<?> packet, CallbackInfo ci) {
//        PacketRecord.addPacket(packet);
//    }
//    @Inject(method = "sendToOtherNearbyPlayers(Lnet/minecraft/network/packet/Packet;)V",at = @At(value = "HEAD"))
//    private void sendToNearbyPlayers2(Packet<?> packet, CallbackInfo ci) {
//        PacketRecord.addPacket(packet);
//    }
//
//    @Inject(method = "sendToOtherNearbyPlayers(Lnet/minecraft/network/packet/Packet;Ljava/util/List;)V",at = @At(value = "HEAD"))
//    private void sendToNearbyPlayers3(Packet<?> packet, List<UUID> except, CallbackInfo ci) {
//        PacketRecord.addPacket(packet);
//    }
//    */
//
//}
