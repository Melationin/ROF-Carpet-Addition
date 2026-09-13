package com.carpet.rof.rules.explosion;

import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class OptimizedExplosionUtil
{
    private static final float MAX_POWER_FACTOR = 1.3F;

    private OptimizedExplosionUtil()
    {
    }

    public static boolean shouldSkipBlockCalculation(ServerExplosion explosion, ExplosionDamageCalculator calculator)
    {
        if (!isApplicable(explosion, calculator))
        {
            OptimizedExplosionStats.onUnavailable();
            return false;
        }

        ServerLevel level = explosion.level();
        Vec3 center = explosion.center();
        float radius = explosion.radius();
        ExplosionMergeData data = ExtraWorldDatas.fromWorld(level).explosionMergeData;
        if (!data.matches(level, center.x, center.y, center.z, radius))
        {
            data.begin(level, center.x, center.y, center.z, radius, explosion.getDirectSourceEntity(), makeEntityBox(center, radius));
        }
        data.count++;

        if (data.blockDamageEmpty)
        {
            OptimizedExplosionStats.onCachedVerdict();
            return true;
        }
        if (data.forceStopped)
        {
            OptimizedExplosionStats.onForceStopped();
            return false;
        }
        if (data.count < OptimizedExplosionSettings.optimizedExplosion)
        {
            OptimizedExplosionStats.onBelowThreshold();
            return false;
        }

        OptimizedExplosionStats.onDryRun();
        if (worstCaseDestroysBlock(explosion, level, calculator, center, radius))
        {
            data.forceStopped = true;
            OptimizedExplosionStats.onUnsafeVerdict();
            return false;
        }
        data.markBlockDamageEmpty();
        return true;
    }

    private static boolean isApplicable(ServerExplosion explosion, ExplosionDamageCalculator calculator)
    {
        if (!OptimizedExplosionSettings.enabled()) return false;
        if (explosion.radius() <= 1.0E-5F) return false;
        return isSupportedCalculator(calculator);
    }

    /**
     * 最坏情况判定只有在"功率越大破坏得越多"成立时才是可靠的上界，
     * 自定义的伤害计算器可能让 shouldBlockExplode 随功率非单调，因此只对原版计算器生效。
     */
    private static boolean isSupportedCalculator(ExplosionDamageCalculator calculator)
    {
        Class<?> type = calculator.getClass();
        return type == ExplosionDamageCalculator.class
                || type == EntityBasedExplosionDamageCalculator.class
                || type == SimpleExplosionDamageCalculator.class;
    }

    /** 与原版 hurtEntities 的查询盒一致 */
    public static AABB makeEntityBox(Vec3 center, float radius)
    {
        float doubleRadius = radius * 2.0F;
        return new AABB(
                Mth.floor(center.x - doubleRadius - 1.0),
                Mth.floor(center.y - doubleRadius - 1.0),
                Mth.floor(center.z - doubleRadius - 1.0),
                Mth.floor(center.x + doubleRadius + 1.0),
                Mth.floor(center.y + doubleRadius + 1.0),
                Mth.floor(center.z + doubleRadius + 1.0));
    }

    /**
     * 把所有射线的初始功率都取上界跑一遍。射线路径只由方向决定、与功率无关（每步固定前进 0.3 格），
     * 所以上界功率访问过的方块是所有实际随机数结果的超集：这里判定为不会破坏方块，则任意一次爆炸都不会破坏方块。
     *
     * 只有非空气方块才算"会破坏方块"：原版会把沿途的空气坐标一并加进 toBlow，但空气在
     * interactWithBlocks 里是空操作（BlockBehaviour.onExplosionHit 第一行就因 state.isAir() 返回），
     * 因此只影响空气的爆炸跳过后不会改变任何方块，代价只是爆炸粒子数量与原版不同。
     */
    public static boolean worstCaseDestroysBlock(Explosion explosion, ServerLevel level, ExplosionDamageCalculator calculator, Vec3 center, float radius)
    {
        float maxPower = radius * MAX_POWER_FACTOR;
        for (int xx = 0; xx < 16; xx++)
        {
            for (int yy = 0; yy < 16; yy++)
            {
                for (int zz = 0; zz < 16; zz++)
                {
                    if (xx != 0 && xx != 15 && yy != 0 && yy != 15 && zz != 0 && zz != 15) continue;
                    double xd = xx / 15.0F * 2.0F - 1.0F;
                    double yd = yy / 15.0F * 2.0F - 1.0F;
                    double zd = zz / 15.0F * 2.0F - 1.0F;
                    double length = Math.sqrt(xd * xd + yd * yd + zd * zd);
                    if (rayDestroysBlock(explosion, level, calculator, center, maxPower, xd / length, yd / length, zd / length)) return true;
                }
            }
        }
        return false;
    }

    private static boolean rayDestroysBlock(Explosion explosion, ServerLevel level, ExplosionDamageCalculator calculator, Vec3 center, float maxPower, double xd, double yd, double zd)
    {
        double x = center.x;
        double y = center.y;
        double z = center.z;
        float power = maxPower;
        while (power > 0.0F)
        {
            BlockPos pos = BlockPos.containing(x, y, z);
            if (!level.isInWorldBounds(pos)) return false;
            BlockState state = level.getBlockState(pos);
            FluidState fluid = level.getFluidState(pos);
            Optional<Float> resistance = calculator.getBlockExplosionResistance(explosion, level, pos, state, fluid);
            if (resistance.isPresent()) power -= (resistance.get() + 0.3F) * 0.3F;
            if (power > 0.0F && !state.isAir() &&
                    calculator.shouldBlockExplode(explosion, level, pos, state, power)
            ) return true;
            // 原版每步的固定衰减（对应 for 的更新表达式），漏掉它在空气里会死循环
            power -= 0.22500001F;
            x += xd * 0.3;
            y += yd * 0.3;
            z += zd * 0.3;
        }
        return false;
    }
}
