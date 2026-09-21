package com.carpet.rof.mixin.blockChange;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.LevelAccessor;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor
{

    @PublicField
    @Unique
    private int blockChangeStamp = 0;

    @Override
    public int rof$getBlockChangeStamp()
    {
        return this.blockChangeStamp;
    }

    @Override
    public void rof$blockChangeStampAdd(){
        this.blockChangeStamp++;
    };

    @Override
    public void rof$setBlockChangeStamp(int value)
    {
        this.blockChangeStamp = value;
    }



}
