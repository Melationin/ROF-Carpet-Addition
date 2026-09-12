package com.carpet.rof.rules.piglinRules;

import com.carpet.rof.rules.mobAi.BCWrapper;
import com.carpet.rof.utils.ROFTool;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.monster.piglin.Piglin;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;

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
        return  !((PiglinEntityAccessor) piglin).rof$getSuppressingAI();
    }
}
