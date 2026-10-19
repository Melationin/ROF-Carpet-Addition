package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin
{
    @Shadow
    private ServerPlayer player;

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerLoad(ServerAdvancementManager manager, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized(this.player))
        {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerSave(CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized(this.player))
        {
            ci.cancel();
        }
    }
}
