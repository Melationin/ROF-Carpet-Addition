package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerTickMixin
{
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;broadcastChanges()V"))
    private void rof$skipFakePlayerContainerBroadcast(AbstractContainerMenu menu, Operation<Void> original)
    {
        if (!FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            original.call(menu);
        }
    }

    @Inject(method = {"trackStartFallingPosition", "trackEnteredOrExitedLavaOnVehicle"}, at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerAdvancementTracking(CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements;flushDirty(Lnet/minecraft/server/level/ServerPlayer;Z)V"))
    private void rof$skipFakePlayerFlushDirty(PlayerAdvancements advancements, ServerPlayer player, boolean show, Operation<Void> original)
    {
        if (!FakePlayerTickUtil.isOptimized(player))
        {
            original.call(advancements, player, show);
        }
    }

    @Inject(method = "awardStat(Lnet/minecraft/stats/Stat;I)V", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerStats(Stat<?> stat, int amount, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }

    @Inject(method = "resetStat(Lnet/minecraft/stats/Stat;)V", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerResetStats(Stat<?> stat, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }

    @WrapOperation(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getContainerSize()I"))
    private int rof$skipFakePlayerInventoryMapScan(Inventory inventory, Operation<Integer> original)
    {
        if (FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            return 0;
        }
        return original.call(inventory);
    }

    @Inject(method = "updateScoreForCriteria", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerScoreCriteria(ObjectiveCriteria criteria, int value, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized((ServerPlayer) (Object) this))
        {
            ci.cancel();
        }
    }
}
