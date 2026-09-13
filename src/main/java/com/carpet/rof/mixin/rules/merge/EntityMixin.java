package com.carpet.rof.mixin.rules.merge;

import com.carpet.rof.rules.merge.EntityTickOrderAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements EntityTickOrderAccessor
{
    @Unique
    long tickOrder = 0;


    @Override
    public long rof$getTickOrder()
    {
        return tickOrder;
    }

    @Override
    public void rof$setTickOrder(long tickOrder)
    {
        this.tickOrder = tickOrder;
    }
}
