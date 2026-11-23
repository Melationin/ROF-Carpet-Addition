package com.carpet.rof.mixin.world.explosion;

import com.carpet.rof.mixinAccessor.ServerLevelExplosionAccess;
import com.carpet.rof.world.explosion.OptimizedExplosionCache;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.world.explosion.ExplosionMergeData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ServerLevelExplosionAccess
{

    @Override
    public void explodeWithCache(
            @Nullable Entity source, double x, double y, double z, float radius,
            Level.ExplosionInteraction interaction, OptimizedExplosionCache cache)
    {
        ((ServerLevel) (Object) this).explode(source, x, y, z, radius, interaction);
    }

    @Override
    public void explodeWithCache(
            @Nullable Entity source, double x, double y, double z, float radius, boolean fire,
            Level.ExplosionInteraction interaction, OptimizedExplosionCache cache)
    {
        ((ServerLevel) (Object) this).explode(source, x, y, z, radius, fire, interaction);
    }

    @Override
    public void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, Vec3 center,
            float radius, boolean fire, Level.ExplosionInteraction interaction, OptimizedExplosionCache cache)
    {
        ((ServerLevel) (Object) this).explode(source, damageSource, damageCalculator, center, radius, fire, interaction);
    }

    @Override
    public void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction, OptimizedExplosionCache cache)
    {
        ((ServerLevel) (Object) this).explode(source, damageSource, damageCalculator, x, y, z, radius, fire, interaction);
    }

    @Override
    public void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction,
            ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles,
            WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> explosionSound,
            OptimizedExplosionCache cache)
    {
        ((ServerLevel) (Object) this).explode(source, damageSource, damageCalculator, x, y, z, radius, fire, interaction,
                smallExplosionParticles, largeExplosionParticles, blockParticles, explosionSound);
    }

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
