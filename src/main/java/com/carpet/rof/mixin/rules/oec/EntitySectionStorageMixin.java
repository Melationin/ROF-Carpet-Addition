package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecSectionAccess;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> {
    @Shadow public abstract @Nullable EntitySection<T> getSection(long key);

    @Inject(method = "createSection(J)Lnet/minecraft/world/level/entity/EntitySection;", at = @At("RETURN"))
    private void rof$rememberSectionKey(long sectionKey, CallbackInfoReturnable<EntitySection<T>> cir) {
        ((OecSectionAccess) cir.getReturnValue()).rof$setSectionKey(sectionKey);
    }

    @Inject(method = "remove(J)V", at = @At("HEAD"))
    private void rof$releaseRemovedSection(long sectionKey, CallbackInfo ci) {
        EntitySection<T> section = this.getSection(sectionKey);
        if (section != null) ((OecSectionAccess) section).rof$releaseGrid();
    }
}
