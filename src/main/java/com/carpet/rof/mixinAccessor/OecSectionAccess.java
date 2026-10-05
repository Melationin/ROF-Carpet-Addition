package com.carpet.rof.mixinAccessor;

import com.carpet.rof.entity.oec.SectionEntityGrid;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface OecSectionAccess {
    void rof$setSectionKey(long sectionKey);

    long rof$sectionKey();

    int rof$entityCount();

    Iterable<Entity> rof$entities();

    @Nullable SectionEntityGrid rof$grid();

    void rof$ensureGrid(boolean fine);

    void rof$clearCells();

    void rof$releaseGrid();
}
