package com.carpet.rof.rules.mobAi;

public class MobAIUtil
{
    private static final ThreadLocal<Integer> ACTIVITY_INDEX = ThreadLocal.withInitial(() -> -1);
    private static final ThreadLocal<Integer> BC_INDEX = ThreadLocal.withInitial(() -> -1);

    public static int activityIndex()
    {
        return ACTIVITY_INDEX.get();
    }

    public static int nextBCIndex()
    {
        int next = BC_INDEX.get() + 1;
        BC_INDEX.set(next);
        return next;
    }

    public static void onActivityDataCreated()
    {
        BC_INDEX.set(-1);
        ACTIVITY_INDEX.set(ACTIVITY_INDEX.get() + 1);
    }

    public static void onBrainCreated()
    {
        ACTIVITY_INDEX.set(0);
        BC_INDEX.set(-1);
    }
}
