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
public class MobMixin
{
    // UNDECIDED 表示未判定，其他负数表示保持 AI。
    @Unique
    private int rof$mobAiRemaining = MobAiSettings.UNDECIDED;

    @Unique
    private boolean rof$mobAiChecked;

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void rof$readMobAi(ValueInput input, CallbackInfo ci)
    {
        int saved = input.getIntOr(MobAiSettings.NBT_KEY, MobAiSettings.UNDECIDED);
        if (saved != MobAiSettings.UNDECIDED) {
            this.rof$mobAiRemaining = saved;
            this.rof$mobAiChecked = true;
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void rof$writeMobAi(ValueOutput output, CallbackInfo ci)
    {
        if (this.rof$mobAiRemaining != MobAiSettings.UNDECIDED) {
            output.putInt(MobAiSettings.NBT_KEY, this.rof$mobAiRemaining);
        }
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
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
        if (BetterNoAiSettings.betterNoAiNbt && this instanceof NoBrainAiAccess access && access.rof$hasNoBrainAi()) {
            // 由 betterNoAi 取消 AI，保持其 noActionTime 语义。
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
        this.rof$mobAiRemaining = self.getRandom().nextDouble() < MobAiSettings.mobAiChance
                ? MobAiSettings.mobAiRestoreTicks
                : MobAiSettings.KEEP_AI;
    }
}
