package com.carpet.rof.mixin.entity.enderPearl;

import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
    @WrapOperation(method = "loadAndSpawnEnderPearl", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;placeEnderPearlTicket(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ChunkPos;)J"))
    private long rof$restorePearlWithoutLoadingTerrain(ServerLevel level, ChunkPos pos, Operation<Long> original,
                                                      @Local Entity pearl)
    {
        long lifetime = ExtraWorldDatas.fromWorld(level).enderPearlTickets.tryPlace(level, pearl);
        return lifetime == 0 ? original.call(level, pos) : lifetime;
    }

    @WrapOperation(method = "registerAndUpdateEnderPearlTicket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;placeEnderPearlTicket(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ChunkPos;)J"))
    private long rof$renewPearlWithoutLoadingTerrain(ServerLevel level, ChunkPos pos, Operation<Long> original,
                                                    @Local(argsOnly = true) ThrownEnderpearl pearl)
    {
        long lifetime = ExtraWorldDatas.fromWorld(level).enderPearlTickets.tryPlace(level, pearl);
        return lifetime == 0 ? original.call(level, pos) : lifetime;
    }
}
