package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.annotation.PublicField;

import com.carpet.rof.mixinAccessor.EntityAccessor;
import com.carpet.rof.rules.explosion.ExplosionMergeData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor
{
    // 0 = 从没算过，-1 = 算过之后实体动过，大于 0 = 算缓存时的批次号
    @PublicField
    @Unique
    private int exposureStamp;

    @PublicField
    @Unique
    private int exposureIndex = -1;

    @Override
    public int rof$getExposureStamp()
    {
        return this.exposureStamp;
    }

    @Override
    public void rof$setExposureStamp(int value)
    {
        this.exposureStamp = value;
    }

    @Override
    public int rof$getExposureIndex()
    {
        return this.exposureIndex;
    }

    @Override
    public void rof$setExposureIndex(int value)
    {
        this.exposureIndex = value;
    }


    @Inject(method = "setPosRaw", at = @At(value = "INVOKE",
                                           target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V",
                                           ordinal = 0))
    private void rof$invalidateExposureOnMove(double x, double y, double z, CallbackInfo ci)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null) data.tryAddEntity((Entity) (Object) this, x, y, z);
        if (this.exposureStamp > 0) this.exposureStamp = -1;
    }

}
