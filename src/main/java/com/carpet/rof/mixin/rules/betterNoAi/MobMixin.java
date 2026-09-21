package com.carpet.rof.mixin.rules.betterNoAi;

import com.carpet.rof.annotation.PublicField;
import com.carpet.rof.mixinAccessor.MobAccessor;
import com.carpet.rof.rules.betterNoAi.BetterNoAiSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
 *///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 只取消 serverAiStep，保留外层 aiStep 的被动运动。
@Mixin(Mob.class)
public abstract class MobMixin implements  MobAccessor
{
    @PublicField
    @Unique
    private boolean noBrainAi;

    @Override
    public boolean rof$getNoBrainAi()
    {
        return this.noBrainAi;
    }

    @Override
    public void rof$setNoBrainAi(boolean value)
    {
        this.noBrainAi = value;
    }


    //? if >=1.21.6 {
    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$readNoBrainAi(ValueInput input, CallbackInfo ci)
    {
        this.noBrainAi = input.getBooleanOr("NoBrainAI", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$writeNoBrainAi(ValueOutput output, CallbackInfo ci)
    {
        if (this.noBrainAi) {
            output.putBoolean("NoBrainAI", true);
        }
    }
    //?} else {
    /*@Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$readNoBrainAi(CompoundTag tag, CallbackInfo ci)
    {
        this.rof$noBrainAi = tag.getBooleanOr("NoBrainAI", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void rof$writeNoBrainAi(CompoundTag tag, CallbackInfo ci)
    {
        if (this.rof$noBrainAi) {
            tag.putBoolean("NoBrainAI", true);
        }
    }
    *///?}

    @Inject(method = "serverAiStep", at = @At(value = "HEAD"), cancellable = true)
    private void rof$suppressAi(CallbackInfo ci)
    {
        if (!BetterNoAiSettings.betterNoAiNbt || !this.noBrainAi) {
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
