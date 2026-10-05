package com.carpet.rof.mixin.entity.drops;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.carpet.rof.entity.drops.DropSettings.optimizedEquipmentDrops;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin
{
    @WrapOperation(
            method = "processEquipmentDropChance",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runIterationOnEquipment(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V"),
            require = 2
    )
    private static void rof$skipUnrelatedDropEnchantments(LivingEntity entity, EnchantmentHelper.EnchantmentInSlotVisitor visitor,
                                                        Operation<Void> original)
    {
        if (!optimizedEquipmentDrops) {
            original.call(entity, visitor);
            return;
        }
        original.call(entity, (EnchantmentHelper.EnchantmentInSlotVisitor) (enchantment, level, item) -> {
            if (!enchantment.value().getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).isEmpty()) {
                visitor.accept(enchantment, level, item);
            }
        });
    }
}
