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
    // createFake 会用 fetchGameProfile 的结果覆盖本地 GameProfile（皮肤），这一步走 sessionserver。
    // 1.21.9 起 carpet 改成按 UUID 解析：返回名称为空的 profile，createFake 会走 p.name().isEmpty()
    // 分支回退到本地构造的 GameProfile。
    // 更早的 carpet 按名字解析且返回 Optional：返回空 Optional，createFake 直接沿用 finalGP。
    //? >=1.21.9 {
    @Inject(method = "fetchGameProfile", at = @At("HEAD"), cancellable = true, require = 1)
    private static void rof$skipNetworkProfile(MinecraftServer server, UUID id,
                                               CallbackInfoReturnable<CompletableFuture<GameProfile>> cir)
    {
        if (ROFTool.DEBUG)
        {
            cir.setReturnValue(CompletableFuture.completedFuture(new GameProfile(id, "")));
        }
    }
    //?} else {
    /*@Inject(method = "fetchGameProfile", at = @At("HEAD"), cancellable = true, require = 1)
    private static void rof$skipNetworkProfile(String name,
                                               CallbackInfoReturnable<CompletableFuture<java.util.Optional<GameProfile>>> cir)
    {
        if (ROFTool.DEBUG)
        {
            cir.setReturnValue(CompletableFuture.completedFuture(java.util.Optional.empty()));
        }
    }
    *///?}
}
