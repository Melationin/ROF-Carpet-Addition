package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerAiStepMixin
{
    // 1.21.9 turned the shoulder handling call in aiStep into the public handleShoulderEntities();
    // 1.21.7/1.21.8 still call the protected removeEntitiesOnShoulder() there. A @Redirect callback
    // would have to invoke that protected method by name from another package (compile error, then
    // IllegalAccessError), so this uses @WrapOperation and forwards through original.call instead.
    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.21.9 {
                    target = "Lnet/minecraft/world/entity/player/Player;handleShoulderEntities()V"
                    //?} else {
                    /*target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"
                    *///?}
            )
    )
    private void optimizedFakePlayerTick$skipShoulderEntities(Player player, Operation<Void> original)
    {
        if (!(FakePlayerTickSettings.optimizedFakePlayerTick && player instanceof EntityPlayerMPFake))
        {
            original.call(player);
        }
    }
}
