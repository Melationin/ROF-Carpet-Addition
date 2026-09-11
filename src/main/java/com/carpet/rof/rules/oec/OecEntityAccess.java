package com.carpet.rof.rules.oec;

import org.jetbrains.annotations.Nullable;

public interface OecEntityAccess {
    @Nullable OecSectionAccess rof$entitySection();

    void rof$setEntitySection(@Nullable OecSectionAccess section);

    long rof$pushStamp();

    void rof$setPushStamp(long stamp);

    int rof$registeredCells();

    void rof$setRegisteredCells(int range);

    boolean rof$pushableBit();

    void rof$setPushableBit(boolean pushable);
}
