package com.carpet.rof.mixin.ISE;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

import static com.carpet.rof.commands.ISECommand.entityIDOverflowPeriod;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow @Final private static AtomicInteger CURRENT_ID;

    @Shadow public abstract World getWorld();

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    void s(EntityType type, World world, CallbackInfo ci){
        if(CURRENT_ID.get() == entityIDOverflowPeriod){
            CURRENT_ID.set(0);
        }
    }
}
