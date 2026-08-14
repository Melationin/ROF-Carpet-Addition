package com.carpet.rof.mixin.debug;

import com.carpet.rof.debug.EnderPearlDebug;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 调试专用 mixin，与正式功能的 {@code rules.enderPearl.ThrownEnderpearlMixin} 完全分开
 * （独立包 com.carpet.rof.mixin.debug，独立注入类）。
 *
 * <p>功能：</p>
 * <ol>
 *   <li>根据珍珠名称（{@code rof:...}，格式见 {@link EnderPearlDebug}）在第一个 tick 修改初速度；</li>
 *   <li>在珍珠自己的 tick() 内计数珍珠tick，并记录世界 gameTime（真实世界tick）；</li>
 *   <li>珍珠落地（被移除，含投掷命中传送、掉入虚空、区块卸载等一切移除路径）时，
 *       输出珍珠内计算的tick与真实的世界tick。</li>
 * </ol>
 *
 * <p>说明：注入点在 tick HEAD / onRemoval HEAD，不取消任何逻辑，与正式功能互不干扰；
 * 即使正式功能的 ThrownEnderpearlMixin 取消了 tick，本 mixin 的计数与落地输出依然生效。</p>
 */
@Mixin(value = ThrownEnderpearl.class,priority = 900)
public abstract class ThrownEnderpearlDebugMixin extends ThrowableItemProjectile
{
    /** 是否是调试珍珠（名称匹配调试前缀）。 */
    @Unique
    private boolean rofDebugActive = false;

    /** 是否已经输出过落地结果（避免重复输出）。 */
    @Unique
    private boolean rofDebugReported = false;

    /** 珍珠内累计的tick数（不含生成tick，即实际飞行tick数）。 */
    @Unique
    private int rofDebugPearlTicks = 0;

    /** 开始计时的世界 gameTime（第一个珍珠tick时记录）。 */
    @Unique
    private long rofDebugWorldTickStart = -1;

    /** 最后一次珍珠tick时观察到的世界 gameTime。 */
    @Unique
    private long rofDebugWorldTickAtLastPearlTick = -1;

    /** 应用过的初速度，用于落地时输出。 */
    @Unique
    private Vec3 rofDebugAppliedVelocity = null;

    /** 珍珠的调试名。 */
    @Unique
    private String rofDebugName = null;

    protected ThrownEnderpearlDebugMixin(EntityType<? extends ThrownEnderpearl> entityType, Level level)
    {
        super(entityType, level);
    }

    /**
     * 每个珍珠tick都经过这里：
     * 1. 第一个tick：判定调试名、记录世界tick起点、按名称修改初速度；
     * 2. 之后每个tick：珍珠内tick计数 +1，并记录当前观察到的世界gameTime。
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void rofDebugTickHead(CallbackInfo ci)
    {
        if (!EnderPearlDebug.ENABLED) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        if (!rofDebugActive) {
            String name = EnderPearlDebug.getDebugName((ThrownEnderpearl)(Object) this);
            if (!EnderPearlDebug.isDebugName(name)) return;

            rofDebugActive = true;
            rofDebugName = name;
            // 世界tick起点：第一个珍珠tick时的 gameTime
            rofDebugWorldTickStart = serverLevel.getGameTime();

            // 按名称修改初速度
            Vec3 velocity = EnderPearlDebug.parseVelocity(name, (ThrownEnderpearl)(Object)this);
            if (velocity != null) {
                rofDebugAppliedVelocity = velocity;
                this.setDeltaMovement(velocity);
            }
            // 生成tick不计入飞行tick，保证珍珠内tick与世界tick可直接比较
            return;
        }

        rofDebugPearlTicks++;
        rofDebugWorldTickAtLastPearlTick = serverLevel.getGameTime();
    }

    /**
     * 珍珠落地（被移除）时输出：珍珠内计算的tick 与 真实的世界tick。
     * 所有移除路径都会走到这里（命中传送 DISCARDED、虚空 KILLED、区块卸载等），
     * 不受正式功能 tick 取消的影响。
     */
    @Inject(method = "onRemoval", at = @At("HEAD"))
    private void rofDebugOnRemoval(Entity.RemovalReason reason, CallbackInfo ci)
    {
        if (!EnderPearlDebug.ENABLED || rofDebugReported) return;
        if (!rofDebugActive) return;

        rofDebugReported = true;
        long worldTickEnd = this.level() instanceof ServerLevel serverLevel
                ? serverLevel.getGameTime()
                : rofDebugWorldTickAtLastPearlTick;

        EnderPearlDebug.reportLanding((ThrownEnderpearl)(Object)this, rofDebugName,
                rofDebugPearlTicks,
                rofDebugWorldTickStart, worldTickEnd,
                rofDebugWorldTickAtLastPearlTick,
                reason.name(), rofDebugAppliedVelocity);
    }
}
