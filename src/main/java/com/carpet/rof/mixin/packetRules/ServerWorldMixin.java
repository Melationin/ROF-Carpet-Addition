package com.carpet.rof.mixin.packetRules;

import com.carpet.rof.rules.entityPacketLimit.EntityPacketLimit;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tick",at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        EntityPacketLimit.entityPacketCountPerTick.clear();
    }

}
