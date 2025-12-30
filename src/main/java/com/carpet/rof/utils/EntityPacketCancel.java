package com.carpet.rof.utils;

import net.minecraft.network.packet.Packet;

import java.util.HashMap;
import java.util.UUID;

public class EntityPacketCancel {

    public final HashMap<UUID, Integer> entityPacketsCountPerTick = new HashMap<>();

    public static final EntityPacketCancel INSTANCE = new EntityPacketCancel();
    public EntityPacketCancel()
    {

    }
}
