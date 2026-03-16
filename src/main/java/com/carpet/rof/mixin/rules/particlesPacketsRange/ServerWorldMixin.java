package com.carpet.rof.mixin.rules.particlesPacketsRange;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.particlesPacketsRange;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{
    @ModifyConstant(
            method = "sendToPlayerIfNearby",
            constant = @Constant(doubleValue = 32.0)
    )
    private double modifyNearbyDistance(double original) {
        return particlesPacketsRange;
    }
}
