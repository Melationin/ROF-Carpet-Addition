package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.rules.explosion.ExplosionMergeData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Inject(method = "setPos(DDD)V", at = @At("HEAD"))
    private void rof$trackExplosionAreaEntry(double x, double y, double z, CallbackInfo ci)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null) data.tryAddEntity((Entity) (Object) this, x, y, z);
    }
}
