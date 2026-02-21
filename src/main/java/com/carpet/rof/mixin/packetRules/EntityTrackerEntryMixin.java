package com.carpet.rof.mixin.packetRules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.packerRules.PacketRulesSettings.tntPacketOptimization;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin
{


    @Shadow @Final private Entity entity;

    @Inject(method = "syncEntityData",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void syncEntityData(CallbackInfo ci)
    {
        if (tntPacketOptimization && entity instanceof TntEntity tntEntity) {
            if (tntEntity.getFuse() > 1) {
                ci.cancel();
            }
        }
    }
}
