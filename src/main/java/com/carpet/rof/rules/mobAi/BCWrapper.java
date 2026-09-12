package com.carpet.rof.rules.mobAi;

import com.carpet.rof.utils.ROFTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public abstract class BCWrapper<E extends LivingEntity> implements BehaviorControl<E>
{

    private final BehaviorControl<E> originalBC;

    public final int activityIndex;
    public final int BCIndex;
    //@Override
    protected abstract boolean condition(E entity);

    public BCWrapper(BehaviorControl<E> originalBC)
    {
        activityIndex = MobAIUtil.activityIndex() ;
        BCIndex = MobAIUtil.nextBCIndex();
        this.originalBC = originalBC;
    }

    @Override
    public Behavior.Status getStatus()
    {
        return originalBC.getStatus();
    }

    @Override
    public @NonNull Set<MemoryModuleType<?>> getRequiredMemories()
    {
        return originalBC.getRequiredMemories();
    }

    @Override
    public boolean tryStart(ServerLevel level, E body, long timestamp)
    {
        //ROFTool.rDEBUG(activityIndex +" " + BCIndex +" "+originalBC.debugString());

        if(condition(body)){
           return originalBC.tryStart(level, body, timestamp);
        }
        return false;
    }

    @Override
    public void tickOrStop(ServerLevel level, E body, long timestamp)
    {
        originalBC.tickOrStop(level, body, timestamp);
    }

    @Override
    public void doStop(ServerLevel level, E body, long timestamp)
    {
        originalBC.doStop(level, body, timestamp);
    }

    @Override
    public @NonNull String debugString()
    {
        return "Wrapper[" + originalBC.debugString() + "]";
    }
}
