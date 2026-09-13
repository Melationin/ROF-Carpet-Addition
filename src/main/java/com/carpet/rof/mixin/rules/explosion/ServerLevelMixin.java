package com.carpet.rof.mixin.rules.explosion;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.explosion.ExplosionMergeData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{

    @Inject(method = "tick", at = @At("HEAD"))
    private void rof$clearExplosionMergeData(BooleanSupplier haveTime, CallbackInfo ci)
    {
        ExtraWorldDatas.fromWorld((ServerLevel) (Object) this).explosionMergeData.clear();
    }


    @Inject(method = "addEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    private void rof$trackSpawnedEntity(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        ExplosionMergeData data = ExplosionMergeData.ACTIVE;
        if (data != null) data.tryAddEntity(entity);
    }
}
