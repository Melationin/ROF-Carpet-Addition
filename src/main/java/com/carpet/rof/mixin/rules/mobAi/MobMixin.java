package com.carpet.rof.mixin.rules.mobAi;

import com.carpet.rof.rules.betterNoAi.BetterNoAiSettings;
import com.carpet.rof.rules.betterNoAi.NoBrainAiAccess;
import com.carpet.rof.rules.mobAi.MobAiSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
    /** 剩余关闭时间；{@link MobAiSettings#UNDECIDED} 表示未判定，负数表示已判定且不关闭 AI。 */
    @Unique
    private int rof$mobAiRemaining = MobAiSettings.UNDECIDED;
    /** 本实例是否已判定过（判定一次，且避免每 tick 重复走过滤器）。 */
    @Unique
    private boolean rof$mobAiChecked;

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void rof$readMobAi(ValueInput input, CallbackInfo ci) {
        int saved = input.getIntOr(MobAiSettings.NBT_KEY, MobAiSettings.UNDECIDED);
        if (saved != MobAiSettings.UNDECIDED) {
            // 存档里已有判定结果 => 不再重掷
            this.rof$mobAiRemaining = saved;
            this.rof$mobAiChecked = true;
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void rof$writeMobAi(ValueOutput output, CallbackInfo ci) {
        if (this.rof$mobAiRemaining != MobAiSettings.UNDECIDED) {
            output.putInt(MobAiSettings.NBT_KEY, this.rof$mobAiRemaining);
        }
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void rof$mobAiStep(CallbackInfo ci) {
        if (!MobAiSettings.enabled()) {
            return;
        }
        if (!this.rof$mobAiChecked) {
            this.rof$mobAiChecked = true;
            if (this.rof$mobAiRemaining == MobAiSettings.UNDECIDED) {
                this.rof$decideMobAi();
            }
        }
        if (this.rof$mobAiRemaining <= 0) {
            return;
        }
        if (--this.rof$mobAiRemaining == 0) {
            // 到点：本 tick 就恢复 AI，并且不再关闭
            this.rof$mobAiRemaining = MobAiSettings.KEEP_AI;
            return;
        }
        if (BetterNoAiSettings.betterNoAiNbt && this instanceof NoBrainAiAccess access && access.rof$hasNoBrainAi()) {
            // 带 NoBrainAI 标签的实体由 betterNoAi 的钩子取消，这里让路，避免 noActionTime 被加两次
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        // 清掉上一次 AI tick 残留的移动输入，否则 xxa/zza 会按 0.98/tick 衰减出十几秒的惯性滑行
        self.xxa = 0.0F;
        self.yya = 0.0F;
        self.zza = 0.0F;
        self.setJumping(false);
        // 保持原版语义：noActionTime 照常累加，600 tick 空闲消亡仍然生效
        self.setNoActionTime(self.getNoActionTime() + 1);
        ci.cancel();
    }

    /**
     * 判定一次。白名单未命中的实体保持 {@link MobAiSettings#UNDECIDED}（不写 NBT）——
     * 它不是本规则的目标，重载后重新判断也不会有别的结果。
     */
    @Unique
    private void rof$decideMobAi() {
        Mob self = (Mob) (Object) this;
        if (!MobAiSettings.mobAiFilter.matches(self)) {
            return;
        }
        // 排除项：幼年生物；随机刻由传送门生成（带传送门冷却）的僵尸猪人
        if (self.isBaby() || self.getPortalCooldown() > 0) {
            this.rof$mobAiRemaining = MobAiSettings.KEEP_AI;
            return;
        }
        this.rof$mobAiRemaining = self.getRandom().nextDouble() < MobAiSettings.mobAiChance
                ? MobAiSettings.mobAiRestoreTicks
                : MobAiSettings.KEEP_AI;
    }
}
