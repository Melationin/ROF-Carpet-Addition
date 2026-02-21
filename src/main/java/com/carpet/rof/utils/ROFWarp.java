package com.carpet.rof.utils;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Optional;

public class ROFWarp
{
    @SuppressWarnings("unchecked")
    public static <T> T getFromNbt(Object s) {
        if (s instanceof Optional<?> opt) {
            return ((Optional<T>) opt).orElse(null);
        }
        return (T) s;
    }

    public static GameMode getGameMode(ServerPlayerEntity player)
    {
        //? >1.21.4 {
        return player.getGameMode();

        //?} else {
            /*return player.interactionManager.getGameMode();
        *///?}
    }

    public static HoverEvent showText(Text text)
    {
        //? >1.21.4 {
        return new HoverEvent.ShowText(text);

        //?} else {
        /*return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
        *///?}
    }

    public static ClickEvent suggestCommand(String s)
    {
        //? >1.21.4 {
        return new ClickEvent.SuggestCommand(s);

        //?} else {
        /*return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,s);
        *///?}
    }

    public static ClickEvent copyToClipboard(String s)
    {
        //? >1.21.4 {
        return new ClickEvent.CopyToClipboard(s);

        //?} else {
        /*return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,s);
        *///?}
    }
    public static PacketType<?> getPacketType(Packet<?> packet)
    {
        //? >1.21.1 {
        return packet.getPacketType();

        //?} else {
        /*return packet.getPacketId();
        *///?}
    }

    public static Iterable<BlockPos> getBlockPosIt(Box box)
    {
        return BlockPos.iterate(
                (int) Math.floor(box.minX),
                (int) Math.floor(box.minY),
                (int) Math.floor(box.minZ),
                (int) Math.ceil(box.maxX),
                (int) Math.ceil(box.maxY),
                (int) Math.ceil(box.maxZ));
    }
    public static World getWorld_(Entity entity) {
        //? if >=1.21.10 {
        /*return entity.getEntityWorld();
         *///?} else {
        return entity.getWorld();
        //?}
    }
    public static Vec3d getPos_(Entity entity) {
        //? if >=1.21.10 {
        /*return entity.getEntityPos();
         *///?} else {
        return entity.getPos();
        //?}
    }
}
