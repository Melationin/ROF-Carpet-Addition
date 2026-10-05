package com.carpet.rof.mixin.world.portal;

import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

import static com.carpet.rof.world.portal.PortalSettings.netherPortalCacheDistance;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin
{
    @WrapOperation(
            method = "getExitPortal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/PortalForcer;findClosestPortalPosition(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/level/border/WorldBorder;)Ljava/util/Optional;")
    )
    private Optional<BlockPos> rof$reuseExitPortal(PortalForcer forcer, BlockPos searchPos, boolean toNether,
                                                 WorldBorder border, Operation<Optional<BlockPos>> original,
                                                 @Local(argsOnly = true) ServerLevel targetLevel,
                                                 @Local(argsOnly = true) Entity entity,
                                                 @Local(argsOnly = true, ordinal = 0) BlockPos entrance)
    {
        double distance = netherPortalCacheDistance;
        if (!(distance > 0)) return original.call(forcer, searchPos, toNether, border);

        var cache = ExtraWorldDatas.fromWorld((ServerLevel) entity.level()).netherPortalCache;
        Vec3 position = entity.position();
        BlockPos cached = cache.get(entrance, targetLevel, position, distance, border);
        if (cached != null) return Optional.of(cached);
        Optional<BlockPos> result = original.call(forcer, searchPos, toNether, border);
        result.ifPresent(exit -> cache.put(entrance, targetLevel, position, exit));
        return result;
    }
}
