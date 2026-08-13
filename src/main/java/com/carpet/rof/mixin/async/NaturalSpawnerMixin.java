package com.carpet.rof.mixin.async;

import com.carpet.rof.utils.asyncWorldgen.spawner.AsyncNaturalSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin
{
    @Inject(method = "spawnForChunk",
            at = @At("HEAD"),
            cancellable = true)
    private static void rof$asyncNaturalSpawning(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state, List<MobCategory> categories, CallbackInfo ci)
    {
        if (AsyncNaturalSpawner.consume(level, chunk, state, categories))
            ci.cancel();
    }
}
