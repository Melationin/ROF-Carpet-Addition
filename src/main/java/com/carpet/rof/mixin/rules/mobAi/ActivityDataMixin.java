package com.carpet.rof.mixin.rules.mobAi;

import com.carpet.rof.rules.mobAi.MobAIUtil;
import net.minecraft.world.entity.ai.ActivityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActivityData.class)
public class ActivityDataMixin
{
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void onInit(final CallbackInfo ci)
    {
        MobAIUtil.onActivityDataCreated();
    }
}
