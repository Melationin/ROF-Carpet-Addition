package com.carpet.rof.mixin.advancement;

import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.advancement.AdvancementSettings.killedTriggerLimitPerTick;

@Mixin(KilledTrigger.class)
public abstract class KilledTriggerMixin
{
    @Inject(
            method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At("HEAD"), cancellable = true
    )
    private void rof$limitKilledTriggers(ServerPlayer player, Entity entity, DamageSource killingBlow, CallbackInfo ci)
    {
        if (killedTriggerLimitPerTick == 0) return;
        if(!ExtraWorldDatas.fromWorld((ServerLevel) ROFWarp.getWorld_(entity)).killedTriggerLimiter.canTriggered()){
            ci.cancel();
        }
    }
}
