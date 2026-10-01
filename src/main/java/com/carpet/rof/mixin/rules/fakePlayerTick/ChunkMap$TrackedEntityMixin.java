package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import static com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings.optimizedFakePlayerTick;

@Mixin(ChunkMap.TrackedEntity.class)
public class ChunkMap$TrackedEntityMixin
{
    @WrapOperation(method = {"sendToTrackingPlayers",
            "sendToTrackingPlayersFiltered"},
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/server/network/ServerPlayerConnection;send(Lnet/minecraft/network/protocol/Packet;)V"
                   ))
    void wrap(ServerPlayerConnection instance, Packet<?> packet, Operation<Void> original){
        if(optimizedFakePlayerTick||instance.getPlayer() instanceof EntityPlayerMPFake){
            return;
        }else {
            original.call(instance,packet);
        }
    }
}
