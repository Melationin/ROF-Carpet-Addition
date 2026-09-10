package com.carpet.rof.rules.oec.lithium;

import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface OecLithiumSectionAccess {
    /** Lithium's lazily created pushable-entity mask, or {@code null} while caching is disabled. */
    @Nullable ReferenceMaskedList<Entity> rof$lithiumPushableEntities();

    /** Runs Lithium's own {@code startFilteringPushableEntities()}, i.e. its cache warm-up. */
    void rof$lithiumStartFilteringPushableEntities();
}
