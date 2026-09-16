package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;


import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerTickMixin
{
    @Unique
    private static boolean isFakeOptimized(ServerPlayer player)
    {
        return FakePlayerTickSettings.optimizedFakePlayerTick && player instanceof EntityPlayerMPFake;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;broadcastChanges()V"))
    private void optimizedFakePlayerTick$skipContainerBroadcast(AbstractContainerMenu menu, Operation<Void> original)
    {
        if (!isFakeOptimized((ServerPlayer) (Object) this))
        {
            original.call(menu);
        }
    }

    //? >=26.2 {
    /*@WrapOperation(method = {"tick", "doTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void optimizedFakePlayerTick$skipPlayerTrigger(PlayerTrigger trigger, ServerPlayer player, Operation<Void> original)
    {
        if (!isFakeOptimized(player))
        {
            original.call(trigger, player);
        }
    }
     *///?}else{

    @WrapOperation(method = {"tick", "doTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/criterion/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void optimizedFakePlayerTick$skipPlayerTrigger(PlayerTrigger trigger, ServerPlayer player, Operation<Void> original)
    {
        if (!isFakeOptimized(player))
        {
            original.call(trigger, player);
        }
    }

    //?}




    //? >=26.2 {
    /*@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/LevitationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void optimizedFakePlayerTick$skipLevitationTrigger(LevitationTrigger trigger, ServerPlayer player, Vec3 start, int duration, Operation<Void> original)
    {
        if (!isFakeOptimized(player))
        {
            original.call(trigger, player, start, duration);
        }
    }
     *///?}else{

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/criterion/LevitationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void optimizedFakePlayerTick$skipLevitationTrigger(LevitationTrigger trigger, ServerPlayer player, Vec3 start, int duration, Operation<Void> original)
    {
        if (!isFakeOptimized(player))
        {
            original.call(trigger, player, start, duration);
        }
    }
    //?}

    @Inject(method = {"trackStartFallingPosition", "trackEnteredOrExitedLavaOnVehicle"}, at = @At("HEAD"), cancellable = true)
    private void optimizedFakePlayerTick$skipAdvancementTracking(CallbackInfo ci)
    {
        if (isFakeOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }


    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements;flushDirty(Lnet/minecraft/server/level/ServerPlayer;Z)V"))
    private void optimizedFakePlayerTick$skipFlushDirty(PlayerAdvancements advancements, ServerPlayer player, boolean show, Operation<Void> original)
    {
        if (!isFakeOptimized(player))
        {
            original.call(advancements, player, show);
        }
    }

    @Inject(method = "awardStat(Lnet/minecraft/stats/Stat;I)V", at = @At("HEAD"), cancellable = true)
    private void optimizedFakePlayerTick$skipStats(Stat<?> stat, int amount, CallbackInfo ci)
    {
        if (isFakeOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }

    @WrapOperation(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getContainerSize()I"))
    private int optimizedFakePlayerTick$skipInventoryMapScan(Inventory inventory, Operation<Integer> original)
    {
        if (isFakeOptimized((ServerPlayer) (Object) this))
        {
            return 0;
        }
        return original.call(inventory);
    }

    @Inject(method = "updateScoreForCriteria", at = @At("HEAD"), cancellable = true)
    private void optimizedFakePlayerTick$skipScoreCriteria(ObjectiveCriteria criteria, int value, CallbackInfo ci)
    {
        if (isFakeOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }
}
