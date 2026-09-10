package com.carpet.rof.mixin.rules.oec.lithium;

import com.carpet.rof.rules.oec.OecMetrics;
import com.carpet.rof.rules.oec.OecSectionStorageAccess;
import com.carpet.rof.rules.oec.OecSettings;
import com.carpet.rof.rules.oec.lithium.LithiumPushCollector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.lithium.common.entity.pushable.EntityPushablePredicate;
import net.caffeinemc.mods.lithium.common.world.ClimbingMobCachingSection;
import net.caffeinemc.mods.lithium.common.world.WorldHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(value = WorldHelper.class, remap = false)
public abstract class WorldHelperMixin {
    @WrapOperation(
            method = "lambda$getPushableEntities$0(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Lnet/caffeinemc/mods/lithium/common/entity/pushable/EntityPushablePredicate;Ljava/util/ArrayList;Lnet/minecraft/world/level/entity/EntitySection;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/lithium/common/world/ClimbingMobCachingSection;lithium$collectPushableEntities(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Lnet/caffeinemc/mods/lithium/common/entity/pushable/EntityPushablePredicate;Ljava/util/ArrayList;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;", remap = false)
    )
    private static AbortableIterationConsumer.Continuation rof$collectPushableEntities(
            ClimbingMobCachingSection section, net.minecraft.world.level.Level world, Entity except, AABB box,
            EntityPushablePredicate<? super Entity> predicate, ArrayList<Entity> output,
            Operation<AbortableIterationConsumer.Continuation> original) {
        if (OecMetrics.ENABLED) OecMetrics.QUERIES.increment();
        if (OecSettings.optimizedEntityCollection && world instanceof ServerLevel serverLevel
                && serverLevel.getServer().isSameThread()
                && LithiumPushCollector.tryCollect(section, world.getGameTime(), except, box, predicate, output)) {
            return AbortableIterationConsumer.Continuation.CONTINUE;
        }
        if (OecMetrics.ENABLED) OecMetrics.LITHIUM_FALLBACKS.increment();
        return original.call(section, world, except, box, predicate, output);
    }

    @WrapOperation(
            method = "getPushableEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/EntitySectionStorage;forEachAccessibleNonEmptySection(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V",
                    remap = false)
    )
    private static void rof$forEachAccessibleNonEmptySection(
            EntitySectionStorage<Entity> storage, AABB box,
            AbortableIterationConsumer<EntitySection<Entity>> consumer, Operation<Void> original) {
        long[] keys = null;
        if (OecSettings.optimizedEntityCollection && storage instanceof OecSectionStorageAccess access) {
            keys = access.rof$spanCache().get(access, box);
        }
        if (keys == null) {
            original.call(storage, box, consumer);
            return;
        }
        for (int i = 0; i < keys.length; i++) {
            EntitySection<Entity> section = storage.getSection(keys[i]);
            if (section != null && !section.isEmpty() && section.getStatus().isAccessible()
                    && consumer.accept(section).shouldAbort()) {
                return;
            }
        }
    }
}
