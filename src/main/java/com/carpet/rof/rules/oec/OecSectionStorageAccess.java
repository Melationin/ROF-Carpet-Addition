package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.longs.LongSortedSet;

public interface OecSectionStorageAccess
{
    LongSortedSet rof$sectionIds();

    OecSectionSpanCache rof$spanCache();
}
