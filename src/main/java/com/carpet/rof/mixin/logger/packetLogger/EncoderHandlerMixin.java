package com.carpet.rof.mixin.logger.packetLogger;

import com.carpet.rof.logger.packetLogger.PacketLogger;
import com.carpet.rof.utils.ROFWarp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.EncoderHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.commands.loggerCommand.PacketLoggerCommand.commandPacketLoggerPlus;

@Mixin(EncoderHandler.class)
public class EncoderHandlerMixin {
    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Lio/netty/buffer/ByteBuf;)V",at = @At(value = "TAIL"))
    public void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf outByteBuf, CallbackInfo ci) {
        if(!commandPacketLoggerPlus.equals("false")
                && ROFWarp.getPacketType(packet).side() ==  NetworkSide.CLIENTBOUND
                &&PacketLogger.instance!=null
                &&PacketLogger.instance.isRunning
        )
            PacketLogger.instance.addPacket(packet, outByteBuf);
    }

}
