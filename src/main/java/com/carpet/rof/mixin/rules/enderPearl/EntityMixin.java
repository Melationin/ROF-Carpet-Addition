package com.carpet.rof.mixin.rules.enderPearl;

import com.carpet.rof.extraWorldData.extraChunkDatas.ExceedChunkMarker;
import com.carpet.rof.utils.ROFWarp;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private boolean rof$mustSkipPearlInsideBlock(BlockPos blockIntersection)
    {
        return optimizedEnderPearlTick
                && (Object) this instanceof ThrownEnderpearl
                && this.level instanceof ServerLevel serverLevel
                && ExceedChunkMarker.mustBeAir(serverLevel, blockIntersection);
    }

    //? if >=1.21.9 {
    @WrapOperation(
            method = "checkInsideBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;Lit/unimi/dsi/fastutil/longs/LongSet;I)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;forEachBlockIntersectedBetween(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/BlockGetter$BlockStepVisitor;)Z"))
    private boolean rof$filterPearlInsideBlocks(Vec3 from, Vec3 to, AABB box, BlockGetter.BlockStepVisitor  visitor, Operation<Boolean> original)
    {
        return original.call(from, to, box, (BlockGetter.BlockStepVisitor)
                (pos, step) -> rof$mustSkipPearlInsideBlock(pos) || visitor.visit(pos, step));
    }


    
    //?} else if >= 1.21.6{
    /*@WrapOperation(method = "checkInsideBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;Lit/unimi/dsi/fastutil/longs/LongSet;)V",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/level/BlockGetter;forEachBlockIntersectedBetween(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/BlockGetter$BlockStepVisitor;)Z"))
    private boolean rof$filterPearlInsideBlocks(Vec3 from, Vec3 to, AABB box, BlockGetter.BlockStepVisitor  visitor, Operation<Boolean> original)
    {
        return original.call(from, to, box, (BlockGetter.BlockStepVisitor)
                (pos, step) -> rof$mustSkipPearlInsideBlock(pos) || visitor.visit(pos, step));
    }




    *///?} else {
    /*@WrapOperation(
            method = "checkInsideBlocks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;forEachBlockIntersectedBetween(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/BlockGetter$BlockStepVisitor;)V"))
    private void rof$filterPearlInsideBlocks(Vec3 from, Vec3 to, AABB box, BlockGetter.BlockStepVisitor visitor, Operation<Void> original)
    {
        original.call(from, to, box, (BlockGetter.BlockStepVisitor) (pos, step) -> {
            if (!rof$mustSkipPearlInsideBlock(pos)) visitor.visit(pos, step);
        });
    }

    *///?}


    //? if<26.1{
    /*@Inject(method = "updateFluidOnEyes", at = @At(value = "HEAD"), cancellable = true)
    private void cancelGetBlockState(CallbackInfo ci){
        if(optimizedEnderPearlTick
                && (Object) this instanceof ThrownEnderpearl
                && this.level instanceof ServerLevel serverLevel) ci.cancel();
    }
    *///?}
}
