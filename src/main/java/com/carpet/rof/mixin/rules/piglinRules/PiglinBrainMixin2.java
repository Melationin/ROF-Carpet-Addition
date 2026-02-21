package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinMax;


@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin2
{

    @Inject(method = "canGather",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void cancelGather(PiglinEntity piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        if (stack.getItem() == Items.GOLD_INGOT) {
            return;
        }
        int count = ((PiglinEntityAccessor) piglin).getNearPiglinCount();
        if (!ROFTool.canLoadAi(piglin.getId(), count, piglinMax)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

