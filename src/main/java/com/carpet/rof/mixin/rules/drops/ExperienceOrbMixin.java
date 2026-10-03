package com.carpet.rof.mixin.rules.drops;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.carpet.rof.rules.drops.DropSettings.optimizedExperienceOrbMerge;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin
{
    @WrapOperation(
            method = "tryMergeToExisting",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")
    )
    private static List<ExperienceOrb> rof$findFirstMergeCandidate(ServerLevel level, EntityTypeTest<Entity, ExperienceOrb> type,
                                                                AABB box, Predicate<? super ExperienceOrb> selector,
                                                                Operation<List<ExperienceOrb>> original)
    {
        if (!optimizedExperienceOrbMerge) {
            return original.call(level, type, box, selector);
        }
        List<ExperienceOrb> result = new ArrayList<>(1);
        level.getEntities(type, box, selector, result, 1);
        return result;
    }
}
