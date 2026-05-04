package com.carpet.rof.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

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

    public static GameType getGameMode(ServerPlayer player)
    {
        //? >1.21.4 {
        return player.gameMode();

        //?} else {
            /*return player.interactionManager.getGameMode();
        *///?}
    }

    public static HoverEvent showText(Component text)
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
        //? >1.21.3 {
        return packet.type();

        //?} else {
        /*return packet.getPacketId();
        *///?}
    }

    public static Iterable<BlockPos> getBlockPosIt(AABB box)
    {
        return BlockPos.betweenClosed(
                (int) Math.floor(box.minX),
                (int) Math.floor(box.minY),
                (int) Math.floor(box.minZ),
                (int) Math.ceil(box.maxX),
                (int) Math.ceil(box.maxY),
                (int) Math.ceil(box.maxZ));
    }
    public static Level getWorld_(Entity entity) {
        //? if >=1.21.9 {
        return entity.level();
         //?} else {
        /*return entity.getWorld();
        *///?}
    }
    public static Vec3 getPos_(Entity entity) {
        //? if >=1.21.9 {
        return entity.position();
         //?} else {
        /*return entity.getPos();
        *///?}
    }
}
