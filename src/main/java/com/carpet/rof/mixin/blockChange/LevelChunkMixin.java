package com.carpet.rof.mixin.blockChange;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.LevelAccessor;
import com.carpet.rof.mixinAccessor.LevelChunkAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements  LevelChunkAccessor
{
    @Shadow
    @Final
    private Level level;

    @PublicField(mutable = false)
    @Unique
    private int rof$blockChangeStamp = 0;

    @Override
    public int rof$getRof$blockChangeStamp()
    {
        return this.rof$blockChangeStamp;
    }

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void rof$bumpBlockChangeStamp(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir)
    {
        this.rof$blockChangeStamp++;
        LevelAccessor.of(level).rof$blockChangeStampAdd();
    }
}
