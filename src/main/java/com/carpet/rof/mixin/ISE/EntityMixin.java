package com.carpet.rof.mixin.ISE;

import net.minecraft.world.entity.Entity;
//? <26.2 {
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
//?}
import org.spongepowered.asm.mixin.Mixin;
//? <26.2 {
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

import static com.carpet.rof.commands.ISECommand.entityIDOverflowPeriod;
//?}

@Mixin(Entity.class)
public abstract class EntityMixin
{
    //? <26.2 {
    @Shadow @Final private static AtomicInteger ENTITY_COUNTER;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    void s(EntityType type, Level world, CallbackInfo ci){
        if(ENTITY_COUNTER.get() == entityIDOverflowPeriod){
            ENTITY_COUNTER.set(0);
        }
    }
    //?}
}
