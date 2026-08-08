package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.rules.piglinRules.PiglinOptimization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends AbstractPiglin implements PiglinEntityAccessor {
    @Unique
    private boolean rof$regularAiActive = true;

    @Unique
    private long rof$nextGroupRefreshTick;

    protected PiglinMixin(EntityType<? extends AbstractPiglin> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean rof$isRegularAiActive() {
        return rof$regularAiActive;
    }

    @Override
    public long rof$getNextGroupRefreshTick() {
        return rof$nextGroupRefreshTick;
    }

    @Override
    public void rof$setOptimizationState(boolean regularAiActive, long nextGroupRefreshTick) {
        rof$regularAiActive = regularAiActive;
        rof$nextGroupRefreshTick = nextGroupRefreshTick;
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void rof$refreshOptimizationGroup(ServerLevel world, CallbackInfo ci) {
        PiglinOptimization.refreshGroupIfNeeded(world, (Piglin) (Object) this);
    }
}
