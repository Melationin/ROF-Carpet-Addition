package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecEntityAccess;
import com.carpet.rof.rules.oec.OecSectionAccess;
import com.carpet.rof.rules.oec.OecUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements OecEntityAccess {
    @Unique private @Nullable OecSectionAccess entitySection;
    @Unique private long pushStamp;
    @Unique private int registeredCells = OecUtil.EMPTY_RANGE;
    @Unique private boolean pushableBit;

    @Override public @Nullable OecSectionAccess rof$getEntitySection() { return this.entitySection; }
    @Override public void rof$setEntitySection(@Nullable OecSectionAccess section) { this.entitySection = section; }
    @Override public long rof$getPushStamp() { return this.pushStamp; }
    @Override public void rof$setPushStamp(long stamp) { this.pushStamp = stamp; }
    @Override public int rof$getRegisteredCells() { return this.registeredCells; }
    @Override public void rof$setRegisteredCells(int range) { this.registeredCells = range; }
    @Override public boolean rof$getPushableBit() { return this.pushableBit; }
    @Override public void rof$setPushableBit(boolean pushable) { this.pushableBit = pushable; }

    @Inject(method = "setBoundingBox(Lnet/minecraft/world/phys/AABB;)V", at = @At("RETURN"))
    private void rof$registerNewCells(AABB box, CallbackInfo ci) {
        if (this.entitySection != null) OecUtil.registerNewCells((Entity) (Object) this);
    }
}
