package com.carpet.rof.mixin.compat;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.carpet.FakePlayerFixSetting;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;


@Pseudo
@Mixin(targets = "dev.dubhe.gugle.carpet.util.BotSpawnUtil", remap = false)
public abstract class GcaBotSpawnUtilMixin
{
    private static final String SPAWN_BOT = "spawnBot(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerLevel;Ldev/dubhe/gugle/carpet/entry/BotInfo;Lcom/mojang/authlib/GameProfile;ZLcarpet/helpers/EntityPlayerActionPack;)Z";
    private static final String TELEPORT_TO = "Lcarpet/patches/EntityPlayerMPFake;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z";

    @WrapWithCondition(
            method = SPAWN_BOT,
            at = @At(value = "INVOKE", target = TELEPORT_TO),
            require = 0
    )
    private static boolean rof$skipGcaSpawnTeleport(
            EntityPlayerMPFake instance,
            ServerLevel level,
            double x,
            double y,
            double z,
            Set<?> relatives,
            float yRot,
            float xRot,
            boolean resetCamera
    )
    {
        if (!FakePlayerFixSetting.gcaBotSpawnKeepVehicle) return true;
        return instance.getVehicle() == null;
    }
}
