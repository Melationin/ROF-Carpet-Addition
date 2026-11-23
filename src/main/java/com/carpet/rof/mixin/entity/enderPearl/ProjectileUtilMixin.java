package com.carpet.rof.mixin.entity.enderPearl;

import com.carpet.rof.entity.enderPearl.EnderPearlSettings;
import com.carpet.rof.entity.enderPearl.PearlEntityRaycaster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin
{
    @Inject(
            method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rof$optimizedPearlEntityHitResult(
            Level level,
            Entity source,
            Vec3 from,
            Vec3 to,
            AABB searchBox,
            Predicate<Entity> filter,
            float margin,
            CallbackInfoReturnable<EntityHitResult> cir)
    {
        if (EnderPearlSettings.optimizedEnderPearlEntityInterception
                && source instanceof ThrownEnderpearl
                && level instanceof ServerLevel serverLevel
                && source.getDeltaMovement().lengthSqr() > 16.0 * 16.0) {
            cir.setReturnValue(PearlEntityRaycaster.getEntityHitResult(
                    serverLevel, source, from, to, searchBox, filter, margin));
        }
    }
}
