package com.carpet.rof.rules.oec;

import org.jetbrains.annotations.Nullable;

public interface OecEntityAccess {
    @Nullable OecSectionAccess rof$getEntitySection();

    void rof$setEntitySection(@Nullable OecSectionAccess section);

    long rof$getPushStamp();

    void rof$setPushStamp(long stamp);

    int rof$getRegisteredCells();

    void rof$setRegisteredCells(int range);

    boolean rof$getPushableBit();

    void rof$setPushableBit(boolean pushable);
}
