package com.carpet.rof.mixinAccessor;

public interface MobAccessor
{
    static MobAccessor of(Object object)
    {
        return (MobAccessor) object;
    }


    boolean rof$getNoBrainAi();

    void rof$setNoBrainAi(boolean value);
}
