package com.carpet.rof.rules.oec.lithium;

import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface OecLithiumSectionAccess
{
    @Nullable
    ReferenceMaskedList<Entity> rof$lithiumPushableEntities();

    void rof$lithiumStartFilteringPushableEntities();
}
