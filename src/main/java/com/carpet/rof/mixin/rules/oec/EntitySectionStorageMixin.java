package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.OecSectionAccess;
import com.carpet.rof.rules.oec.OecSectionSpanCache;
import com.carpet.rof.rules.oec.OecSectionStorageAccess;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> implements OecSectionStorageAccess {
    @Shadow public abstract @Nullable EntitySection<T> getSection(long key);
    @Shadow @Final private LongSortedSet sectionIds;
    @Unique private OecSectionSpanCache rof$spanCache;

    @Override public LongSortedSet rof$sectionIds() { return this.sectionIds; }

    @Override
    public OecSectionSpanCache rof$spanCache() {
        OecSectionSpanCache cache = this.rof$spanCache;
        if (cache == null) this.rof$spanCache = cache = new OecSectionSpanCache();
        return cache;
    }

    @Inject(method = "createSection(J)Lnet/minecraft/world/level/entity/EntitySection;", at = @At("RETURN"))
    private void rof$rememberSectionKey(long sectionKey, CallbackInfoReturnable<EntitySection<T>> cir) {
        ((OecSectionAccess) cir.getReturnValue()).rof$setSectionKey(sectionKey);
        // A section created inside a cached span makes that snapshot incomplete; a removed one never does.
        this.rof$spanCache().invalidateIfContains(sectionKey);
    }

    @Inject(method = "remove(J)V", at = @At("HEAD"))
    private void rof$releaseRemovedSection(long sectionKey, CallbackInfo ci) {
        EntitySection<T> section = this.getSection(sectionKey);
        if (section != null) ((OecSectionAccess) section).rof$releaseGrid();
    }
}
