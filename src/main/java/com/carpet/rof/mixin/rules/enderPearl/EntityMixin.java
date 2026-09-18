package com.carpet.rof.mixin.rules.enderPearl;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.utils.ROFWarp;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

import static com.carpet.rof.rules.enderPearl.EnderPearlSettings.optimizedEnderPearlTick;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    private Level level;

    @Inject(method = "lambda$checkInsideBlocks$0", at = @At("HEAD"), cancellable = true)
    private void rof$skipPearlInsideBlockScan(int maxMovementIterations, AtomicInteger iterations, boolean debugEntityBlockIntersections, Vec3 from, Vec3 _to, LongSet visitedBlocks, boolean movedFar, AABB deflatedBoundingBoxAtTarget, InsideBlockEffectApplier.StepBasedCollector effectCollector, BlockPos blockIntersection, int iteration, CallbackInfoReturnable<Boolean> cir)
    {
        if( optimizedEnderPearlTick && (Object)this instanceof ThrownEnderpearl){
            if(this.level instanceof ServerLevel serverLevel) {
                if (ExceedChunkMarker.mustBeAir(serverLevel, blockIntersection)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }

    }
}
