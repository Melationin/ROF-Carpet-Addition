package com.carpet.rof.mixin.rules.mobAi;

import com.carpet.rof.rules.mobAi.MobAIUtil;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Brain.Provider.class)
public class BrainProviderMixin
{
    @Inject(method = "makeBrain",
            at = @At(value = "HEAD"))
    private void onMakeBrain(final CallbackInfoReturnable<Brain<?>> cir)
    {
        MobAIUtil.onBrainCreated();
    }
}
