package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.rules.explosion.ExposureCacheAccess;
import com.carpet.rof.rules.explosion.ExplosionMergeData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements ExposureCacheAccess
{
    // 0 = 从没算过，-1 = 算过之后实体动过，大于 0 = 算缓存时的批次号
    @Unique
    private int exposureStamp;
    @Unique
    private int exposureIndex = -1;

    @Override
    public int rof$getExposureStamp()
    {
        return this.exposureStamp;
    }

    @Override
    public void rof$setExposureStamp(int stamp)
    {
        this.exposureStamp = stamp;
    }

    @Override
    public int rof$getExposureIndex()
    {
        return this.exposureIndex;
    }

    @Override
    public void rof$setExposureIndex(int index)
    {
        this.exposureIndex = index;
    }

    @Inject(method = "setPos(DDD)V", at = @At("HEAD"))
    private void rof$trackExplosionAreaEntry(double x, double y, double z, CallbackInfo ci)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null) data.tryAddEntity((Entity) (Object) this, x, y, z);
    }

    @Inject(method = "setPosRaw", at = @At("HEAD"))
    private void rof$invalidateExposureOnMove(double x, double y, double z, CallbackInfo ci)
    {
        if (this.exposureStamp > 0) this.exposureStamp = -1;
    }
    
    @Inject(method = "setBoundingBox", at = @At("HEAD"))
    private void rof$invalidateExposureOnResize(AABB box, CallbackInfo ci)
    {
        if (this.exposureStamp > 0) this.exposureStamp = -1;
    }
}
