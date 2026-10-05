package com.carpet.rof.entity.mobAi.piglinRules;

import com.carpet.rof.mixinAccessor.PiglinAccessor;
import com.carpet.rof.entity.mobAi.BCWrapper;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.monster.piglin.Piglin;

public class PiglinBCWrapper extends BCWrapper<Piglin>
{

    private boolean tradeUseful;

    public PiglinBCWrapper(BehaviorControl<Piglin> originalBC)
    {
        super(originalBC);
        tradeUseful = false;
        if(activityIndex==0){
            if(BCIndex==5 || BCIndex==6) tradeUseful = true;
        }
        if(activityIndex==2){
            if(BCIndex==1 || BCIndex==2) tradeUseful = true;
        }
    }

    @Override
    public boolean condition(Piglin piglin)
    {
        if(tradeUseful) return true;
        return  !PiglinAccessor.of(piglin).rof$getSuppressingAI();
    }
}
