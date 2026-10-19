package com.carpet.rof.mixin.carpet.fakePlayerList;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.carpet.fakePlayerList.FakePlayerGroups;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    // 假人被移除后需要补发一次。
    @Inject(method = "remove", at = @At("TAIL"), require = 1)
    private void rof$resyncFakePlayerGroups(ServerPlayer player, CallbackInfo ci)
    {
        if (!FakePlayerGroups.enabled() || !(player instanceof EntityPlayerMPFake))
        {
            return;
        }
        FakePlayerGroups.resync(((PlayerList) (Object) this).getPlayers());
    }
}
