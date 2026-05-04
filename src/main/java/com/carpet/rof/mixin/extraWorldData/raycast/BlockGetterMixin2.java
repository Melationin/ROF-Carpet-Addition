package com.carpet.rof.mixin.extraWorldData.raycast;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.optimizeRaycast;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin2
{


    @Shadow
    BlockState getBlockState(BlockPos pos);

    @Shadow
    FluidState getFluidState(BlockPos pos);

    @Shadow @Nullable BlockHitResult clipWithInteractionOverride(Vec3 start, Vec3 end, BlockPos pos, VoxelShape shape, BlockState state);

    @Shadow
    static <T, C> T traverseBlocks(Vec3 start, Vec3 end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory){throw new AssertionError();};

    @Unique
    private BlockHitResult blockHitFactory(ClipContext innerContext, BlockPos pos){


        BlockState blockState = this.getBlockState(pos);
        FluidState fluidState = this.getFluidState(pos);
        Vec3 vec3d = innerContext.getFrom();
        Vec3 vec3d2 = innerContext.getTo();
        VoxelShape voxelShape = innerContext.getBlockShape(blockState, (ServerLevel)(Object)this, pos);
        BlockHitResult blockHitResult = this.clipWithInteractionOverride(vec3d, vec3d2, pos, voxelShape, blockState);
        VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (ServerLevel)(Object)this, pos);
        BlockHitResult blockHitResult2 = voxelShape2.clip(vec3d, vec3d2, pos);
        double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getFrom().distanceToSqr(blockHitResult.getLocation());
        double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getFrom().distanceToSqr(blockHitResult2.getLocation());
        return d <= e ? blockHitResult : blockHitResult2;
    }

    @Unique
    private BlockHitResult missFactory(ClipContext innerContext, BlockPos pos){
        Vec3 vec3d = innerContext.getFrom().subtract(innerContext.getTo());
        return BlockHitResult.miss(innerContext.getTo(), Direction.getApproximateNearest(vec3d.x, vec3d.y, vec3d.z), BlockPos.containing(innerContext.getTo()));
    }

    @Inject(method = "clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;",
            at = @At(value = "HEAD"),
            cancellable = true)
    default void raycastOp(ClipContext context, CallbackInfoReturnable<BlockHitResult> cir)
    {
        if(optimizeRaycast&& ExceedChunkMarkerSetting.exceedChunkMarker){
            if((Object)this instanceof ServerLevel serverWorld) {
                cir.setReturnValue(
                        (BlockHitResult) traverseBlocks(context.getFrom(), context.getTo(), context, (innerContext, pos) ->
                        {
                            if (serverWorld.isOutsideBuildHeight(pos)) {
                                return null;
                            }
                            if (ExceedChunkMarker.mustBeAir(serverWorld, pos)) {
                                return null;
                            }

                            BlockState blockState = this.getBlockState(pos);
                            FluidState fluidState = this.getFluidState(pos);
                            Vec3 vec3d = innerContext.getFrom();
                            Vec3 vec3d2 = innerContext.getTo();
                            VoxelShape voxelShape = innerContext.getBlockShape(blockState, (Level) this, pos);
                            BlockHitResult blockHitResult = this.clipWithInteractionOverride(vec3d, vec3d2, pos, voxelShape,
                                    blockState);
                            VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (Level) this, pos);
                            BlockHitResult blockHitResult2 = voxelShape2.clip(vec3d, vec3d2, pos);
                            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getFrom()
                                    .distanceToSqr(blockHitResult.getLocation());
                            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getFrom()
                                    .distanceToSqr(blockHitResult2.getLocation());
                            return d <= e ? blockHitResult : blockHitResult2;
                        }, (innerContext) ->
                        {
                            Vec3 vec3d = innerContext.getFrom().subtract(innerContext.getTo());
                            return BlockHitResult.miss(innerContext.getTo(),
                                    Direction.getApproximateNearest(vec3d.x, vec3d.y, vec3d.z),
                                    BlockPos.containing(innerContext.getTo()));
                        }));
            }
        }
    }


}
