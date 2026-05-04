package com.carpet.rof.mixin.rules.particlesPacketsRange;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.particlesPacketsRange;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @ModifyConstant(
            method = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z",
            constant = @Constant(doubleValue = 32.0)
    )
    private double modifyNearbyDistance(double original) {
        return particlesPacketsRange;
    }
}
