package com.carpet.rof.mixin.rules.piglinRules;

import com.carpet.rof.rules.piglinRules.PiglinEntityAccessor;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;


//这个Mixin在有锂时毫无意义
@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "HEAD"), cancellable = true)
    private static void adjustMovementCancel(@Nullable Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3> cir){
        if(entity instanceof Piglin piglin){
            if (((PiglinEntityAccessor) piglin).rof$getSuppressingAI()) {
                cir.setReturnValue(new Vec3(0,0,0));
                cir.cancel();
            }
        }
    }
}
