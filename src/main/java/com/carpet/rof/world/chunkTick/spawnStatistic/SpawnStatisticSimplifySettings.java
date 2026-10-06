package com.carpet.rof.world.chunkTick.spawnStatistic;

import carpet.CarpetServer;
import com.carpet.rof.mixinAccessor.ServerLevelAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Set;

public class SpawnStatisticSimplifySettings
{
    private static Set<ResourceKey<Level>> simplifiedWorlds = Set.of();

    static void apply(Set<ResourceKey<Level>> worlds)
    {
        simplifiedWorlds = worlds;
        MinecraftServer server = CarpetServer.minecraft_server;
        if (server == null) {
            return;
        }
        for (ServerLevel world : server.getAllLevels()) {
            ServerLevelAccessor.of(world).rof$setSpawnStatisticSimplified(isSimplified(world));
        }
    }

    public static boolean isSimplified(Level world)
    {
        return simplifiedWorlds.contains(world.dimension());
    }
}
