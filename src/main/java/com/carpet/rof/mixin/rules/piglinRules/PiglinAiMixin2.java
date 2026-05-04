package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;


@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin2
{

    @Inject(method = "wantsToPickup",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void cancelGather(Piglin piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        if (stack.getItem() == Items.GOLD_INGOT) {
            return;
        }
        int count = ((PiglinEntityAccessor) piglin).getNearPiglinCount();
        if (!ROFTool.canLoadAi(piglin.getId(), count, piglinStackingAISuppression)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

