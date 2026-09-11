package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecCells;
import com.carpet.rof.rules.oec.OecEntityAccess;
import com.carpet.rof.rules.oec.OecRegistration;
import com.carpet.rof.rules.oec.OecSectionAccess;
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
    @Unique private @Nullable OecSectionAccess rof$entitySection;
    @Unique private long rof$pushStamp;
    @Unique private int rof$registeredCells = OecCells.EMPTY_RANGE;
    @Unique private boolean rof$pushableBit;

    @Override public @Nullable OecSectionAccess rof$entitySection() { return this.rof$entitySection; }
    @Override public void rof$setEntitySection(@Nullable OecSectionAccess section) { this.rof$entitySection = section; }
    @Override public long rof$pushStamp() { return this.rof$pushStamp; }
    @Override public void rof$setPushStamp(long stamp) { this.rof$pushStamp = stamp; }
    @Override public int rof$registeredCells() { return this.rof$registeredCells; }
    @Override public void rof$setRegisteredCells(int range) { this.rof$registeredCells = range; }
    @Override public boolean rof$pushableBit() { return this.rof$pushableBit; }
    @Override public void rof$setPushableBit(boolean pushable) { this.rof$pushableBit = pushable; }

    @Inject(method = "setBoundingBox(Lnet/minecraft/world/phys/AABB;)V", at = @At("RETURN"))
    private void rof$registerNewCells(AABB box, CallbackInfo ci) {
        if (this.rof$entitySection != null) OecRegistration.registerNewCells((Entity) (Object) this);
    }
}
