package com.carpet.rof.mixin.blockChange;

import com.carpet.rof.blockChange.LevelBlockChangeAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelBlockChangeAccess
{
    @Unique
    private int rof$blockChangeStamp = 0;

    @Override
    public int rof$getBlockChangeStamp()
    {
        return this.rof$blockChangeStamp;
    }

    @Override
    public void rof$blockChangeStampAdd(){
        this.rof$blockChangeStamp++;
    };

}
