package com.carpet.rof.mixin.entity.mobAi;

import com.carpet.rof.entity.mobAi.MobAiSettings;
import com.carpet.rof.utils.NBTHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin
{
    // UNDECIDED 表示未判定，其他负数表示保持 AI。
    @Unique
    private int rof$mobAiRemaining = MobAiSettings.UNDECIDED;
    @Unique
    private boolean noBrainAi;
    @Unique
    private boolean rof$mobAiChecked;

    @Inject(method = "readAdditionalSaveData",
            at = @At("HEAD"))
    private void rof$readMobAi(@Coerce Object input, CallbackInfo ci)
    {
        this.noBrainAi = NBTHelper.getBooleanOr(input, "NoBrainAI", false);
        int saved = NBTHelper.getIntOr(input, MobAiSettings.NBT_KEY, MobAiSettings.UNDECIDED);
        if (saved != MobAiSettings.UNDECIDED) {
            this.rof$mobAiRemaining = saved;
            this.rof$mobAiChecked = true;
        }
    }

    @Inject(method = "addAdditionalSaveData",
            at = @At("HEAD"))
    private void rof$writeMobAi(@Coerce Object output, CallbackInfo ci)
    {
        if (this.noBrainAi) {
            NBTHelper.putBoolean(output, "NoBrainAI", true);
        }
        if (this.rof$mobAiRemaining != MobAiSettings.UNDECIDED) {
            NBTHelper.putInt(output, MobAiSettings.NBT_KEY, this.rof$mobAiRemaining);
        }
    }

    @Inject(method = "serverAiStep",
            at = @At("HEAD"),
            cancellable = true)
    private void rof$mobAiStep(CallbackInfo ci)
    {
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
            this.rof$mobAiRemaining = MobAiSettings.KEEP_AI;
            return;
        }
        if (MobAiSettings.betterNoAiNbt && this.noBrainAi) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        // 清除残留移动输入，避免关闭 AI 后继续滑行。
        self.xxa = 0.0F;
        self.yya = 0.0F;
        self.zza = 0.0F;
        self.setJumping(false);

        self.setNoActionTime(self.getNoActionTime() + 1);
        ci.cancel();
    }

    // 未命中列表时不写 NBT，重载后允许重新匹配。
    @Unique
    private void rof$decideMobAi()
    {
        Mob self = (Mob) (Object) this;
        if (!MobAiSettings.mobAiFilter.matches(self)) {
            return;
        }

        if (self.isBaby() || self.getPortalCooldown() > 0) {
            this.rof$mobAiRemaining = MobAiSettings.KEEP_AI;
            return;
        }
        this.rof$mobAiRemaining = self.getRandom()
                .nextDouble() < MobAiSettings.mobAIDelayChance ? MobAiSettings.mobAIDelayTicks : MobAiSettings.KEEP_AI;
    }

    @Inject(method = "serverAiStep",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void rof$suppressAi(CallbackInfo ci)
    {
        if (!MobAiSettings.mobAiOptimizations || !MobAiSettings.betterNoAiNbt || !this.noBrainAi) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        // 清除残留移动输入，避免关闭 AI 后继续滑行。
        self.xxa = 0.0F;
        self.yya = 0.0F;
        self.zza = 0.0F;
        self.setJumping(false);
        ci.cancel();
    }
}
