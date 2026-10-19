package com.carpet.rof.carpet.fakePlayerTick;

import carpet.patches.EntityPlayerMPFake;

public final class FakePlayerTickUtil
{
    public static boolean isOptimized(Object holder)
    {
        return FakePlayerTickSettings.optimizedFakePlayerTick && holder instanceof EntityPlayerMPFake;
    }

    private FakePlayerTickUtil()
    {
    }
}
