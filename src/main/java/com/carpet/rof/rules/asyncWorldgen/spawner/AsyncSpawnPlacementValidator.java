package com.carpet.rof.rules.asyncWorldgen.spawner;

import com.carpet.rof.rules.asyncWorldgen.LoadedBlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;

final class AsyncSpawnPlacementValidator
{
    private AsyncSpawnPlacementValidator()
    {
    }

    static Result validate(ServerLevel level, EntityType<?> type, BlockPos pos)
    {
        SpawnPlacementType placement = SpawnPlacements.getPlacementType(type);
        if (placement != SpawnPlacementTypes.NO_RESTRICTIONS && placement != SpawnPlacementTypes.IN_WATER && placement != SpawnPlacementTypes.IN_LAVA && placement != SpawnPlacementTypes.ON_GROUND)
            return Result.UNAVAILABLE;
        if (placement == SpawnPlacementTypes.NO_RESTRICTIONS)
            return Result.ALLOWED;
        if (!level.getWorldBorder().isWithinBounds(pos))
            return Result.DENIED;
        LoadedBlockGetter blocks = new LoadedBlockGetter(level);
        if (placement == SpawnPlacementTypes.IN_LAVA)
            return result(blocks, blocks.getFluidState(pos).is(FluidTags.LAVA) ? Result.ALLOWED : Result.DENIED);
        if (placement == SpawnPlacementTypes.IN_WATER) {
            BlockPos above = pos.above();
            boolean water = blocks.getFluidState(pos).is(FluidTags.WATER);
            BlockState state = blocks.getBlockState(above);
            Result result = !water ? Result.DENIED : !vanilla(state) ? Result.PARTIAL : state.isRedstoneConductor(
                    blocks, above) ? Result.DENIED : Result.ALLOWED;
            return result(blocks, result);
        }
        BlockPos below = pos.below(), above = pos.above();
        BlockState belowState = blocks.getBlockState(below), state = blocks.getBlockState(
                pos), aboveState = blocks.getBlockState(above);
        Result result;
        if (!empty(state, type) || !empty(aboveState, type))
            result = Result.DENIED;
        else if (!vanilla(belowState) || !vanilla(state) || !vanilla(aboveState))
            result = Result.PARTIAL;
        else if (!belowState.isValidSpawn(blocks, below, type))
            result = Result.DENIED;
        else if (!NaturalSpawner.isValidEmptySpawnBlock(blocks, pos, state, state.getFluidState(), type))
            result = Result.DENIED;
        else
            result = NaturalSpawner.isValidEmptySpawnBlock(blocks, above, aboveState, aboveState.getFluidState(),
                    type) ? Result.ALLOWED : Result.DENIED;
        return result(blocks, result);
    }

    private static Result result(LoadedBlockGetter blocks, Result result)
    {
        return blocks.isAvailable() ? result : Result.UNAVAILABLE;
    }

    private static boolean empty(BlockState state, EntityType<?> type)
    {
        return !state.isSignalSource() && state.getFluidState().isEmpty() && !state.is(
                BlockTags.PREVENT_MOB_SPAWNING_INSIDE) && !type.isBlockDangerous(state);
    }

    private static boolean vanilla(BlockState state)
    {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("minecraft");
    }

    enum Result {ALLOWED, PARTIAL, DENIED, UNAVAILABLE}
}
