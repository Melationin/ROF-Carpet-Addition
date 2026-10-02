package com.carpet.rof.mixinAccessor;

public interface EntityTypeAccessor
{
    static EntityTypeAccessor of(Object object)
    {
        return (EntityTypeAccessor) object;
    }

    boolean rof$getAsyncSpawnRule();

    void rof$setAsyncSpawnRule(boolean value);
}
