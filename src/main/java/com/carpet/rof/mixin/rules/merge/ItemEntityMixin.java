package com.carpet.rof.mixin.rules.merge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.carpet.rof.rules.merge.MergeSetting.canPartialMerge;
import static com.carpet.rof.rules.merge.MergeSetting.optimizedItemMerge;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{

    @WrapOperation(
            method = "tryToMerge",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;areMergable(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private static boolean allowPartialMerge(ItemStack selfStack, ItemStack otherStack, Operation<Boolean> original)
    {
        if (original.call(selfStack, otherStack)) return true;
        return optimizedItemMerge && canPartialMerge(selfStack, otherStack);
    }
}
