package com.carpet.rof.rules.oec;

import it.unimi.dsi.fastutil.longs.LongSortedSet;

/** Exposes the per-storage section span cache to the Lithium push path. */
public interface OecSectionStorageAccess {
    /** The storage's live section keys (vanilla {@code EntitySectionStorage.sectionIds}). */
    LongSortedSet rof$sectionIds();

    /** Four-slot FIFO cache of "query box -> section keys of its spanned sections". */
    OecSectionSpanCache rof$spanCache();
}
