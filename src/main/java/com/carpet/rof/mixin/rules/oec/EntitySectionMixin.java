package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.*;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySection.class)
public abstract class EntitySectionMixin<T extends EntityAccess> implements OecSectionAccess {
    @Shadow @Final private ClassInstanceMultiMap<T> storage;
    @Unique private long rof$sectionKey = Long.MIN_VALUE;
    @Unique private @Nullable SectionEntityGrid rof$grid;

    @Override public void rof$setSectionKey(long sectionKey) { this.rof$sectionKey = sectionKey; }
    @Override public long rof$sectionKey() { return this.rof$sectionKey; }
    @Override public int rof$entityCount() { return this.storage.size(); }
    @Override public @Nullable SectionEntityGrid rof$grid() { return this.rof$grid; }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Entity> rof$entities() { return (Iterable<Entity>) (Object) this.storage; }

    @Override
    public void rof$ensureGrid(boolean fine) {
        SectionEntityGrid grid = this.rof$grid;
        if (grid != null && grid.isFine() == fine) return;
        if (grid != null) {
            OecGridRegistry.unregister(this);
            grid.release();
        }
        this.rof$grid = new SectionEntityGrid(fine);
        OecGridRegistry.register(this);
    }

    @Override public void rof$clearCells() {
        SectionEntityGrid grid = this.rof$grid;
        if (grid != null) grid.clear();
    }

    @Override public void rof$releaseGrid() {
        SectionEntityGrid grid = this.rof$grid;
        if (grid != null) {
            OecGridRegistry.unregister(this);
            grid.release();
            this.rof$grid = null;
        }
    }

    @Inject(method = "add(Lnet/minecraft/world/level/entity/EntityAccess;)V", at = @At("RETURN"))
    private void rof$onEntityAdded(T entityLike, CallbackInfo ci) {
        if (entityLike instanceof Entity entity) {
            OecEntityAccess access = (OecEntityAccess) entity;
            access.rof$setEntitySection(this);
            access.rof$setRegisteredCells(OecUtil.EMPTY_RANGE);
            OecUtil.registerNewCells(entity);
        }
    }

    @Inject(method = "remove(Lnet/minecraft/world/level/entity/EntityAccess;)Z", at = @At("RETURN"))
    private void rof$onEntityRemoved(T entityLike, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && entityLike instanceof Entity entity) {
            OecEntityAccess access = (OecEntityAccess) entity;
            if (access.rof$entitySection() == this) access.rof$setEntitySection(null);
        }
    }
}
