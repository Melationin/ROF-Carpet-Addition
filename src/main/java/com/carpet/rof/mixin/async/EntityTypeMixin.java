package com.carpet.rof.mixin.async;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.EntityTypeAccessor;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements EntityTypeAccessor
{
    @PublicField
    @Unique
    private boolean asyncSpawnRule;

    @Override
    public boolean rof$getAsyncSpawnRule()
    {
        return this.asyncSpawnRule;
    }

    @Override
    public void rof$setAsyncSpawnRule(boolean value)
    {
        this.asyncSpawnRule = value;
    }
}
