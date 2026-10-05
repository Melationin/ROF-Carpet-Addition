package com.carpet.rof.mixinAccessor;

import com.carpet.rof.entity.oec.OecSectionSpanCache;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

public interface OecSectionStorageAccess
{
    LongSortedSet rof$sectionIds();

    OecSectionSpanCache rof$spanCache();
}
