package com.carpet.rof.mixin.rules.betterNoAi;

import com.carpet.rof.rules.betterNoAi.BetterNoAiSettings;
import com.carpet.rof.rules.betterNoAi.NoBrainAiAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 只取消 serverAiStep，保留外层 aiStep 的被动运动。
@Mixin(Mob.class)
public class MobMixin implements NoBrainAiAccess
{
    @Unique
    private boolean rof$noBrainAi;

    @Override
    public boolean rof$hasNoBrainAi()
    {
        return this.rof$noBrainAi;
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$readNoBrainAi(ValueInput input, CallbackInfo ci)
    {
        this.rof$noBrainAi = input.getBooleanOr("NoBrainAI", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$writeNoBrainAi(ValueOutput output, CallbackInfo ci)
    {
        if (this.rof$noBrainAi) {
            output.putBoolean("NoBrainAI", true);
        }
    }

    @Inject(method = "serverAiStep", at = @At(value = "HEAD"), cancellable = true)
    private void rof$suppressAi(CallbackInfo ci)
    {
        if (!BetterNoAiSettings.betterNoAiNbt || !this.rof$noBrainAi) {
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
