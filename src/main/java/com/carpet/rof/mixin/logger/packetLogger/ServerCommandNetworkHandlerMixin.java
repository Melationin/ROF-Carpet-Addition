package com.carpet.rof.mixin.logger.packetLogger;

import com.carpet.rof.logger.packetLogger.PacketLogger;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


public class ServerCommandNetworkHandlerMixin {

    //@Inject(method = "send",at = @At(value = "HEAD"))
    private void send(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci){

    }
}
