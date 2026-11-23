package com.carpet.rof.mixinAccessor;

import com.carpet.rof.world.explosion.OptimizedExplosionCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

public interface ServerLevelExplosionAccess
{
    static ServerLevelExplosionAccess of(ServerLevel level)
    {
        return (ServerLevelExplosionAccess) level;
    }

    void explodeWithCache(
            @Nullable Entity source, double x, double y, double z, float radius,
            Level.ExplosionInteraction interaction, OptimizedExplosionCache cache);

    void explodeWithCache(
            @Nullable Entity source, double x, double y, double z, float radius, boolean fire,
            Level.ExplosionInteraction interaction, OptimizedExplosionCache cache);

    void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, Vec3 center,
            float radius, boolean fire, Level.ExplosionInteraction interaction, OptimizedExplosionCache cache);

    void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction, OptimizedExplosionCache cache);

    void explodeWithCache(
            @Nullable Entity source, @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction,
            ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles,
            WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> explosionSound,
            OptimizedExplosionCache cache);
}
