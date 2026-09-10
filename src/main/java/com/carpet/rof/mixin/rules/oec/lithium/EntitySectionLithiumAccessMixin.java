package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.lithium.OecLithiumSectionAccess;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

// priority 1100 用于访问 Lithium 合入的成员；这些成员使用 remap = false。
@Mixin(value = EntitySection.class, priority = 1100)
public interface EntitySectionLithiumAccessMixin extends OecLithiumSectionAccess
{
    @Override
    @Accessor(value = "pushableEntities", remap = false)
    @Nullable
    ReferenceMaskedList<Entity> rof$lithiumPushableEntities();

    @Override
    @Invoker(value = "startFilteringPushableEntities", remap = false)
    void rof$lithiumStartFilteringPushableEntities();
}
