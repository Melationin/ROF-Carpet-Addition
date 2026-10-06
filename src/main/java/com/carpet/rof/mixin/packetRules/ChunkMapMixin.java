package com.carpet.rof.mixin.packetRules;

import com.carpet.rof.mixinAccessor.ChunkMapRecoveryAccessor;
import com.carpet.rof.mixinAccessor.TrackedEntityRecoveryAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.packetRules.PacketRulesSettings.packetLimits;
import static com.carpet.rof.packetRules.PacketRulesSettings.entityTrackingRecoveryTicks;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements ChunkMapRecoveryAccessor
{
    @Shadow @Final private Int2ObjectMap<ChunkMap.TrackedEntity> entityMap;

    @Unique private boolean rof$hasSpawnLimitedEntities;

    @Override
    public void rof$markSpawnLimitedEntity()
    {
        rof$hasSpawnLimitedEntities = true;
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void recoverSpawnLimitedEntities(CallbackInfo ci)
    {
        if (!rof$hasSpawnLimitedEntities) return;
        if (packetLimits && entityTrackingRecoveryTicks < 0) return;
        boolean remaining = false;
        for (var trackedEntity : entityMap.values()) {
            remaining |= ((TrackedEntityRecoveryAccessor) trackedEntity).rof$recoverSpawnLimit();
        }
        rof$hasSpawnLimitedEntities = remaining;
    }
}
