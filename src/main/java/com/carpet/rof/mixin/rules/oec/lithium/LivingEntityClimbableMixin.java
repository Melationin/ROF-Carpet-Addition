package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.ClimbableTagCacheSettings;
import com.carpet.rof.rules.oec.lithium.ClimbableBlockStateCache;
import com.carpet.rof.rules.oec.lithium.ClimbableStateCacheAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClimbableMixin {
    @WrapOperation(
            method = "onClimbable()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z",
                    ordinal = 1
            )
    )
    private boolean rof$cacheClimbableTag(BlockState state, TagKey<Block> tag, Operation<Boolean> original) {
        if (!ClimbableTagCacheSettings.optimizedClimbableTagCheck || tag != BlockTags.CLIMBABLE
                || !(state instanceof ClimbableStateCacheAccess cache)) {
            return original.call(state, tag);
        }

        int epoch = ClimbableBlockStateCache.epoch();
        if (cache.rof$getClimbableCacheEpoch() == epoch) {
            return cache.rof$getClimbableCacheValue();
        }

        boolean value = original.call(state, tag);
        cache.rof$setClimbableCache(epoch, value);
        return value;
    }
}
