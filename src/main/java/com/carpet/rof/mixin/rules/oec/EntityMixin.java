package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecEntityAccess;
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

    @Override public OecSectionAccess rof$getEntitySection() { return this.rof$entitySection; }
    @Override public void rof$setEntitySection(OecSectionAccess section) { this.rof$entitySection = section; }

    @Inject(method = "setBoundingBox(Lnet/minecraft/world/phys/AABB;)V", at = @At("RETURN"))
    private void rof$updateIndexedBounds(AABB box, CallbackInfo ci) {
        OecSectionAccess section = this.rof$entitySection;
        if (section != null) section.rof$updateEntityBounds((Entity) (Object) this, box);
    }
}
