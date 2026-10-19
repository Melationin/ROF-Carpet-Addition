package com.carpet.rof.mixin.carpet.fakePlayerSpawn;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.utils.ROFTool;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeProfileMixin
{
    @Inject(method = "fetchGameProfile", at = @At("HEAD"), cancellable = true, require = 1)
    private static void rof$skipNetworkProfile(MinecraftServer server, UUID id,
                                               CallbackInfoReturnable<CompletableFuture<GameProfile>> cir)
    {
        if (ROFTool.DEBUG)
        {
            cir.setReturnValue(CompletableFuture.completedFuture(new GameProfile(id, "")));
        }
    }
}
