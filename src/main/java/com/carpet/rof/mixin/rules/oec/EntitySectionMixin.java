package com.carpet.rof.mixin.rules.oec;

import com.carpet.rof.rules.oec.*;
import net.minecraft.core.SectionPos;
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
    @Unique private long rof$nextPolicyTick;
    @Unique private @Nullable SectionEntityGrid rof$grid;

    @Override public void rof$setSectionKey(long sectionKey) { this.rof$sectionKey = sectionKey; }

    @Override
    public @Nullable SectionEntityGrid rof$prepareGrid(long gameTime) {
        if (!OecSettings.optimizedEntityCollection || this.rof$sectionKey == Long.MIN_VALUE) {
            rof$releaseGrid();
            return null;
        }
        if (gameTime >= this.rof$nextPolicyTick) {
            this.rof$nextPolicyTick = gameTime + 200L;
            if (this.rof$grid != null && (this.storage.size() < 25 || !this.rof$grid.isValid())) rof$releaseGrid();
        }
        if (this.rof$grid == null && this.storage.size() > 50) {
            this.rof$grid = rof$buildGrid();
            if (this.rof$grid != null) OecGridRegistry.register(this);
        }
        return this.rof$grid;
    }

    @Unique
    private @Nullable SectionEntityGrid rof$buildGrid() {
        SectionEntityGrid candidate = new SectionEntityGrid(SectionPos.x(this.rof$sectionKey), SectionPos.y(this.rof$sectionKey), SectionPos.z(this.rof$sectionKey));
        for (T entityLike : this.storage) {
            if (!(entityLike instanceof Entity entity) || !candidate.add(entity)) {
                candidate.release();
                return null;
            }
            ((OecEntityAccess) entity).rof$setEntitySection(this);
        }
        candidate.enableFineGrid();
        OecMetrics.REBUILDS.increment();
        return candidate;
    }

    @Override public void rof$updateEntityBounds(Entity entity, net.minecraft.world.phys.AABB box) {
        SectionEntityGrid grid = this.rof$grid;
        if (grid != null) grid.updateBounds(entity, box);
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
            ((OecEntityAccess) entity).rof$setEntitySection(this);
            if (this.rof$grid != null) this.rof$grid.add(entity);
        }
    }

    @Inject(method = "remove(Lnet/minecraft/world/level/entity/EntityAccess;)Z", at = @At("RETURN"))
    private void rof$onEntityRemoved(T entityLike, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue()) && entityLike instanceof Entity entity) {
            if (this.rof$grid != null) this.rof$grid.remove(entity);
            OecEntityAccess access = (OecEntityAccess) entity;
            if (access.rof$getEntitySection() == this) access.rof$setEntitySection(null);
        }
    }
}
