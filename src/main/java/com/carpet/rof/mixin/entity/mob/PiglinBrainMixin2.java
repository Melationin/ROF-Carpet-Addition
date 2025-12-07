package com.carpet.rof.mixin.entity.mob;

import com.carpet.rof.accessor.PiglinEntityAccessor;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.carpet.rof.ROFCarpetSettings.piglinMax;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin2 {
    @Inject(at = @At(value = "TAIL"),method = "create" )
    private static void piglinBrain(PiglinEntity piglin, Brain<PiglinEntity> brain, CallbackInfoReturnable<Brain<?>> cir) {
        //brain.getPossibleActivities().clear();
        //brain.getPossibleActivities().add(RemoveOffHandItemTask.create());
    }

    @Inject(method = "canGather",at = @At(value = "HEAD"),cancellable = true)
    private static void cancelGather(PiglinEntity piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.getItem() == Items.GOLD_INGOT){
            return;
        }
        int count =  ((PiglinEntityAccessor)piglin).getNearPiglinCount();
        if(count> piglinMax && Math.random()*count>=1){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

