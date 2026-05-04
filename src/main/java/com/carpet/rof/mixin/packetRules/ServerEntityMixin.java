package com.carpet.rof.mixin.packetRules;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.tntPacketOptimization;

@Mixin(ServerEntity.class)
public class ServerEntityMixin
{


    @Shadow @Final private Entity entity;

    @Inject(method = "sendDirtyEntityData",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void syncEntityData(CallbackInfo ci)
    {
        if (tntPacketOptimization && entity instanceof PrimedTnt tntEntity) {
            if (tntEntity.getFuse() > 1) {
                ci.cancel();
            }
        }
    }
}
