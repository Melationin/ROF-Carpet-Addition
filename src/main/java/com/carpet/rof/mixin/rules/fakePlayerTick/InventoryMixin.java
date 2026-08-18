package com.carpet.rof.mixin.rules.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.fakePlayerTick.FakePlayerTickSettings;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin
{
    @Shadow
    private int selected;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void optimizedFakePlayerTick$mainHandOnly(CallbackInfo ci)
    {
        if (FakePlayerTickSettings.optimizedFakePlayerTick && ((Inventory) (Object) this).player instanceof EntityPlayerMPFake)
        {
            ItemStack stack = ((Inventory) (Object) this).getItem(this.selected);
            if (!stack.isEmpty())
            {
                stack.inventoryTick(((Inventory) (Object) this).player.level(), ((Inventory) (Object) this).player, EquipmentSlot.MAINHAND);
            }
            ci.cancel();
        }
    }
}
