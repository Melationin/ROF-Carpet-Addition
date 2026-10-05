package com.carpet.rof.mixin.entity.oec.lithium;

import com.carpet.rof.mixinAccessor.ClimbableStateCacheAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin implements ClimbableStateCacheAccess
{
    @Unique
    private int climbableCacheEpoch;

    @Unique
    private boolean climbableCacheValue;

    @Override
    public int rof$getClimbableCacheEpoch()
    {
        return this.climbableCacheEpoch;
    }

    @Override
    public boolean rof$getClimbableCacheValue()
    {
        return this.climbableCacheValue;
    }

    @Override
    public void rof$setClimbableCache(int epoch, boolean value)
    {
        this.climbableCacheValue = value;
        this.climbableCacheEpoch = epoch;
    }
}
