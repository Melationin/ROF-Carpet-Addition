package com.carpet.rof.mixin.entity.oec;

import com.carpet.rof.mixinAccessor.OecStorageHolder;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelEntityGetterAdapter.class)
public abstract class LevelEntityGetterAdapterMixin implements OecStorageHolder {
    @Shadow @Final private EntitySectionStorage<?> sectionStorage;

    @Override
    public EntitySectionStorage<?> rof$entitySectionStorage() { return this.sectionStorage; }
}
