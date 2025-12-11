package com.carpet.rof.mixin.entity;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

import static com.carpet.rof.ROFCarpetSettings.optimizeItemMerge;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getStack();

    @Shadow @Nullable public UUID owner;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At(value = "HEAD"),cancellable = true)
    private void tryMerge(ItemEntity itemEntity, CallbackInfo ci) {
        if(!optimizeItemMerge )     return;
        ItemEntity targetEntity = (ItemEntity) (Object) this;
        ItemStack targetEntityStack = this.getStack();
        ItemEntity otherEntity = itemEntity;
        ItemStack otherStack = itemEntity.getStack();
        int targetCount = targetEntityStack.getCount();
        int otherCount = otherStack.getCount();
        if (    !targetEntityStack.isStackable() || !otherStack.isStackable()||
                targetCount >= targetEntityStack.getMaxCount() ||
                !ItemStack.areItemsAndComponentsEqual(targetEntityStack, otherStack)||
                !Objects.equals(this.owner, otherEntity.owner)
        ) {
            ci.cancel();
            return;
        }
        if (otherCount > targetCount) {
            targetEntity = itemEntity;
            targetEntityStack = otherStack;
            targetCount = otherCount;
            otherEntity = (ItemEntity) (Object) this;
            otherStack = this.getStack();
            otherCount = otherStack.getCount();
        }
        int MoveCount = targetCount +  otherCount > targetEntityStack.getMaxCount() ? targetEntityStack.getMaxCount() - targetCount :  otherCount;
        targetEntityStack.setCount(targetCount+MoveCount );
        otherStack.setCount( otherCount - MoveCount);
        targetEntity.pickupDelay = Math.max(targetEntity.pickupDelay, otherEntity.pickupDelay);
        targetEntity.itemAge = Math.min(targetEntity.itemAge, otherEntity.itemAge);
        if (otherEntity.getStack().isEmpty()) {
            otherEntity.discard();
        }
        ci.cancel();
    }
}
