package com.carpet.rof.mixin.entity.drops;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.carpet.rof.entity.drops.DropSettings.optimizedEquipmentDrops;

@Mixin(Mob.class)
public abstract class MobMixin
{
    @WrapOperation(
            method = "dropCustomDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processEquipmentDropChance(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)F")
    )
    private float rof$skipEmptyEquipmentDropChance(ServerLevel level, LivingEntity attacker, DamageSource source,
                                                 float chance, Operation<Float> original, @Local ItemStack itemStack)
    {
        if (optimizedEquipmentDrops && itemStack.isEmpty()) {
            return chance;
        }
        return original.call(level, attacker, source, chance);
    }
}
