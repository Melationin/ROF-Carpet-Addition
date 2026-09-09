package com.carpet.rof.rules.oec;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface OecSectionAccess {
    void rof$setSectionKey(long sectionKey);
    @Nullable SectionEntityGrid rof$prepareGrid(long gameTime);
    void rof$updateEntityBounds(Entity entity, AABB box);
    void rof$releaseGrid();
}
