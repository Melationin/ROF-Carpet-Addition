package com.carpet.rof.mixin.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.ROFCarpetSettings.mobAIDelay;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin extends HostileEntity {

    @Unique
    public int livingTime = 0;

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals",at = @At(value = "HEAD"),cancellable = true)
    private void initGoalsCancel(CallbackInfo ci) {
        if(livingTime <mobAIDelay) ci.cancel();
    }

    @Inject(method = "tick",at = @At(value = "HEAD"),cancellable = true)
    private void tickMix(CallbackInfo ci) {
        livingTime++;
        if(livingTime == mobAIDelay) this.initGoals();
    }
}
