package com.carpet.rof.mixin.packetRules;

import com.carpet.rof.rules.packerRules.TrackedEntityRecoveryAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSeconds;
import static com.carpet.rof.rules.packerRules.PacketRulesSettings.entitySpawnPacketLimitSecondsRecoverTime;

@Mixin(ChunkMap.class)
public class ChunkMapMixin
{
    @Shadow @Final private Int2ObjectMap<ChunkMap.TrackedEntity> entityMap;

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void recoverSpawnLimitedEntities(CallbackInfo ci)
    {
        if (entitySpawnPacketLimitSeconds < 0 || entitySpawnPacketLimitSecondsRecoverTime < 0) return;
        for (var trackedEntity : entityMap.values()) {
            ((TrackedEntityRecoveryAccessor) trackedEntity).rof$recoverSpawnLimit();
        }
    }
}
