package com.carpet.rof.rules.oec;

import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;

public final class OecPushableFilter {
    private OecPushableFilter() {}

    /** 与 Lithium 的 MAYBE_PUSHABLE 同义：Entity.isPushable 的默认实现返回 false，重写者才可能可推。 */
    public static boolean maybePushable(Entity entity) {
        return PushableEntityClassGroup.MAYBE_PUSHABLE.contains(entity);
    }

    /** 与 Lithium 的 entityPushableHeuristic 同源：身处可攀爬方块内视为不可推，其余情况交给谓词判定。 */
    public static boolean currentlyPushable(Entity entity) {
        if (!PushableEntityClassGroup.CACHABLE_UNPUSHABILITY.contains(entity)) return true;
        return !entity.getInBlockState().is(BlockTags.CLIMBABLE);
    }
}
