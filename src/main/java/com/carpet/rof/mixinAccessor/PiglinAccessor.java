package com.carpet.rof.mixinAccessor;

public interface PiglinAccessor
{
    static PiglinAccessor of(Object object)
    {
        return (PiglinAccessor) object;
    }

    boolean rof$getSuppressingAI();

}
