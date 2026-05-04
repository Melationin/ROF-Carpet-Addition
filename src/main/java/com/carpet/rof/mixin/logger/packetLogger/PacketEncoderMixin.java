package com.carpet.rof.mixin.logger.packetLogger;

import com.carpet.rof.logger.packetLogger.PacketLogger;
import com.carpet.rof.utils.ROFWarp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.commands.loggerCommand.PacketLoggerCommand.commandPacketLoggerPlus;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin
{
    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "TAIL"))
    public void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf outByteBuf, CallbackInfo ci) {
        if(!commandPacketLoggerPlus.equals("false")
                && ROFWarp.getPacketType(packet).flow() ==  PacketFlow.CLIENTBOUND
                &&PacketLogger.instance!=null
                &&PacketLogger.instance.isRunning
        )
            PacketLogger.instance.addPacket(packet, outByteBuf);
    }

}
