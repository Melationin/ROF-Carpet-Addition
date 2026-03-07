package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;

@Mixin(Entity.class)
public class EntityMixin
{

    //这个会造成非常异常的情况：猪灵浮空
    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "HEAD"), cancellable = true)
    private void adjustMovementCancel(Vec3d movement, CallbackInfoReturnable<Vec3d> cir){
        if((Object)(this) instanceof PiglinEntity piglin){
            int count = ((PiglinEntityAccessor) piglin).getNearPiglinCount();
            if (!ROFTool.canLoadAi(piglin.getId(), count, piglinStackingAISuppression)


            ) {
                cir.setReturnValue(Vec3d.ZERO);
                cir.cancel();
            }
        }
    }
}
