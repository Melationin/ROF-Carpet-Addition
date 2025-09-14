package com.carpet.rof.mixin.entity.mob;

import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinEntity.class)
public class PiglinEntityMixin {

    @Inject(method = "canGather",at = @At(value = "HEAD"),cancellable = true)
    public void canGather(ServerWorld world, ItemStack stack, CallbackInfoReturnable<Boolean> cir){

    }
}
