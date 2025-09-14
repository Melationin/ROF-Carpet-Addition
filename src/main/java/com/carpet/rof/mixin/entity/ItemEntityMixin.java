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
        if (
                targetEntityStack.getCount() >= targetEntityStack.getMaxCount() ||
                !ItemStack.areItemsAndComponentsEqual(targetEntityStack, otherStack)||
                !Objects.equals(this.owner, otherEntity.owner)
        ) {
            ci.cancel();
            return;
        }
        if (otherStack.getCount() > targetEntityStack.getCount()) {
            targetEntity = itemEntity;
            targetEntityStack = otherStack;
            otherEntity = (ItemEntity) (Object) this;
            otherStack = this.getStack();
        }
        int T1 = targetEntityStack.getCount();
        int T2 = otherEntity.getStack().getCount();
        int Max = targetEntityStack.getMaxCount();
        int MoveCount = T1 + T2 > Max ? targetEntityStack.getMaxCount() - T1 : T2;

        targetEntityStack.setCount(MoveCount + T1);
        otherStack.setCount(T2 - MoveCount);

        targetEntity.pickupDelay = Math.max(targetEntity.pickupDelay, otherEntity.pickupDelay);
        targetEntity.itemAge = Math.min(targetEntity.itemAge, otherEntity.itemAge);
        if (otherEntity.getStack().isEmpty()) {
            otherEntity.discard();
        }
        ci.cancel();
    }
}
