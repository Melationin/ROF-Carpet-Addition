package com.carpet.rof.advancement;

public final class KilledTriggerLimiter
{
    private int calls;

    public void clear(){
        calls = 0;
    }
    public boolean canTriggered(){
        calls ++;
        return calls <= AdvancementSettings.killedTriggerLimitPerTick;
    }
}
