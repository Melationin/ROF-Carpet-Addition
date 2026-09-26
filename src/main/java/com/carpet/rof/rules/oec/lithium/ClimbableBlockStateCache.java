package com.carpet.rof.rules.oec.lithium;



public final class ClimbableBlockStateCache
{
    private static volatile int epoch = 1;

    private ClimbableBlockStateCache()
    {
    }

    public static int epoch()
    {
        return epoch;
    }

    public static void invalidate()
    {
        int next = epoch + 1;
        epoch = next == 0 ? 1 : next;
    }
}
