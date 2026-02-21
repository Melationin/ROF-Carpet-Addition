package com.carpet.rof.utils;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPacketCancel {

    public final Map<UUID, Integer> entityPacketsCountPerTick = new ConcurrentHashMap<>();

    public static final EntityPacketCancel INSTANCE = new EntityPacketCancel();
}
