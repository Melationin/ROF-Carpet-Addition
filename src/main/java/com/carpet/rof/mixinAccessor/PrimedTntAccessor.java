package com.carpet.rof.mixinAccessor;

public interface PrimedTntAccessor
{
    static PrimedTntAccessor of(Object object)
    {
        return (PrimedTntAccessor) object;
    }
    void rof$addMergeCount(int mergeCount);

    int rof$getMergedTNTNCount();

    void rof$setMergedTNTNCount(int value);
}
