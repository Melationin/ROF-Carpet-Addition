package com.carpet.rof.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.PistonBlock.EXTENDED;
import static net.minecraft.state.property.Properties.FACING;


@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {
    @Shadow protected abstract boolean shouldExtend(RedstoneView world, BlockPos pos, Direction pistonFace);

    @Inject(method = "tryMove",at = @At(value = "HEAD"),cancellable = true)
    private void tryMove(World world, BlockPos pos, BlockState state, CallbackInfo ci){

    }

}
