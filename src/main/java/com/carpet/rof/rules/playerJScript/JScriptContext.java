package com.carpet.rof.rules.playerJScript;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class JScriptContext {

    public MinecraftServer server;

    public ServerPlayerEntity player = null;

    public JScriptContext(MinecraftServer server,ServerPlayerEntity player) {
        this.server = server;
        this.player = player;

    }
}
