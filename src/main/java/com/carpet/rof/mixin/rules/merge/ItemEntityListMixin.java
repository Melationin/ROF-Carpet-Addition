package com.carpet.rof.mixin.rules.merge;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.caffeinemc.mods.lithium.common.entity.item.ItemEntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.carpet.rof.rules.merge.MergeSetting.optimizeItemMerge;


@Mixin(ItemEntityList.class)
public abstract class ItemEntityListMixin
{
    @ModifyExpressionValue(
            method = "consumeForEntityStacking",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I")
    )
    private int useAllSameTypeItems(int original)
    {
        return optimizeItemMerge ? 0 : original;
    }
}
