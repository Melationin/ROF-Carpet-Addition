package com.carpet.rof.mixin.rules.merge;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.EntityAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor
{
    @PublicField
    @Unique
    private long tickOrder = 0;

    @Override
    public long rof$getTickOrder()
    {
        return this.tickOrder;
    }

    @Override
    public void rof$setTickOrder(long value)
    {
        this.tickOrder = value;
    }


}
