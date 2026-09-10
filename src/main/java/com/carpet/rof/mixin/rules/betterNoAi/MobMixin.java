package com.carpet.rof.mixin.rules.betterNoAi;

import com.carpet.rof.rules.betterNoAi.BetterNoAiSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 「更好的NoAI NBT」：实体带 NoBrainAI 标签时停止 AI 行为，但保留被动运动。
 * <p>
 * 只钩 {@link Mob#serverAiStep()}：它是怪物唯一的 AI 总入口（sensing / targetSelector / goalSelector /
 * navigation / customServerAiStep(含 brain) / moveControl / lookControl / jumpControl），
 * 而 {@code LivingEntity.aiStep} 里的 travel（重力、流体）与 {@code pushEntities}（互推、挤压伤害）
 * 都在它之外，因此被动运动不受影响。
 * <p>
 * 刻意不使用原版 {@code NoAI}：那会连 {@code isEffectiveAi()} 一起关掉，
 * 使 {@code LivingEntity.aiStep} 跳过 travel —— 实体将悬空静止且不再受水流/击退影响。
 */
@Mixin(Mob.class)
public class MobMixin {
    /** NoBrainAI 标记，随实体 NBT 读写，服务端本地字段（不同步客户端、不占用 entityData）。 */
    @Unique
    private boolean rof$noBrainAi;

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$readNoBrainAi(ValueInput input, CallbackInfo ci) {
        this.rof$noBrainAi = input.getBooleanOr("NoBrainAI", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$writeNoBrainAi(ValueOutput output, CallbackInfo ci) {
        if (this.rof$noBrainAi) {
            output.putBoolean("NoBrainAI", true);
        }
    }

    @Inject(method = "serverAiStep", at = @At(value = "HEAD"), cancellable = true)
    private void rof$suppressAi(CallbackInfo ci) {
        if (!BetterNoAiSettings.betterNoAiNbt || !this.rof$noBrainAi) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        // 清掉上一次 AI tick 残留的移动输入，否则 xxa/zza 会按 0.98/tick 衰减，产生最长约 14 秒的惯性滑行
        self.xxa = 0.0F;
        self.yya = 0.0F;
        self.zza = 0.0F;
        self.setJumping(false);
        ci.cancel();
    }
}
