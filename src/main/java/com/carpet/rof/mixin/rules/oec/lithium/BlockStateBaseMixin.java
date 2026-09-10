package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.lithium.ClimbableStateCacheAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin implements ClimbableStateCacheAccess
{
    @Unique
    private int rof$climbableCacheEpoch;

    @Unique
    private boolean rof$climbableCacheValue;

    @Override
    public int rof$getClimbableCacheEpoch()
    {
        return this.rof$climbableCacheEpoch;
    }

    @Override
    public boolean rof$getClimbableCacheValue()
    {
        return this.rof$climbableCacheValue;
    }

    @Override
    public void rof$setClimbableCache(int epoch, boolean value)
    {
        this.rof$climbableCacheValue = value;
        this.rof$climbableCacheEpoch = epoch;
    }
}
