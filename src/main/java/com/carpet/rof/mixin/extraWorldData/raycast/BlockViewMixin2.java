package com.carpet.rof.mixin.extraWorldData.raycast;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
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

@Mixin(BlockView.class)
public interface BlockViewMixin2 {


    @Shadow BlockState getBlockState(BlockPos pos);

    @Shadow FluidState getFluidState(BlockPos pos);

    @Shadow @Nullable BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state);

    @Shadow
    static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory){throw new AssertionError();};

    @Unique
    private  BlockHitResult blockHitFactory(RaycastContext innerContext,BlockPos pos){


        BlockState blockState = this.getBlockState(pos);
        FluidState fluidState = this.getFluidState(pos);
        Vec3d vec3d = innerContext.getStart();
        Vec3d vec3d2 = innerContext.getEnd();
        VoxelShape voxelShape = innerContext.getBlockShape(blockState, (ServerWorld)(Object)this, pos);
        BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, pos, voxelShape, blockState);
        VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (ServerWorld)(Object)this, pos);
        BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, pos);
        double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
        double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
        return d <= e ? blockHitResult : blockHitResult2;
    }

    @Unique
    private BlockHitResult missFactory(RaycastContext innerContext,BlockPos pos){
        Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
        return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
    }

    @Inject(method = "raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;",
            at = @At(value = "HEAD"),
            cancellable = true)
    default void raycastOp(RaycastContext context, CallbackInfoReturnable<BlockHitResult> cir)
    {
        if(optimizeRaycast&& ExceedChunkMarkerSetting.exceedChunkMarker){
            if((Object)this instanceof ServerWorld serverWorld) {
                cir.setReturnValue(
                        (BlockHitResult) raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) ->
                        {
                            if (serverWorld.isOutOfHeightLimit(pos)) {
                                return null;
                            }
                            if (ExceedChunkMarker.mustBeAir(serverWorld, pos)) {
                                return null;
                            }

                            BlockState blockState = this.getBlockState(pos);
                            FluidState fluidState = this.getFluidState(pos);
                            Vec3d vec3d = innerContext.getStart();
                            Vec3d vec3d2 = innerContext.getEnd();
                            VoxelShape voxelShape = innerContext.getBlockShape(blockState, (World) this, pos);
                            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, pos, voxelShape,
                                    blockState);
                            VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (World) this, pos);
                            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, pos);
                            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart()
                                    .squaredDistanceTo(blockHitResult.getPos());
                            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart()
                                    .squaredDistanceTo(blockHitResult2.getPos());
                            return d <= e ? blockHitResult : blockHitResult2;
                        }, (innerContext) ->
                        {
                            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
                            return BlockHitResult.createMissed(innerContext.getEnd(),
                                    Direction.getFacing(vec3d.x, vec3d.y, vec3d.z),
                                    BlockPos.ofFloored(innerContext.getEnd()));
                        }));
            }
        }
    }


}
