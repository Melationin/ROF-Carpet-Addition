package com.carpet.rof.mixin.carpet.fakePlayerTick;

import com.carpet.rof.carpet.fakePlayerTick.FakePlayerTickUtil;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.TrackedEntity.class)
public abstract class ChunkMapTrackedEntityMixin
{
    @Inject(method = "updatePlayer", at = @At("HEAD"), cancellable = true)
    private void rof$skipFakePlayerObserver(ServerPlayer player, CallbackInfo ci)
    {
        if (FakePlayerTickUtil.isOptimized(player))
        {
            ci.cancel();
        }
    }
}
