package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerAiStepMixin
{
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;handleShoulderEntities()V"))
    private void optimizedFakePlayerTick$skipShoulderEntities(Player player)
    {
        if (!(FakePlayerTickSettings.optimizedFakePlayerTick && player instanceof EntityPlayerMPFake))
        {
            player.handleShoulderEntities();
        }
    }
}
