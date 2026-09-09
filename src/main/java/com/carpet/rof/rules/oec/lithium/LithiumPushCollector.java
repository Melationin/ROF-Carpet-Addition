package com.carpet.rof.rules.oec.lithium;

import net.caffeinemc.mods.lithium.common.entity.pushable.EntityPushablePredicate;
import net.caffeinemc.mods.lithium.common.util.collections.ReferenceMaskedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.AABB;
import com.carpet.rof.rules.oec.OecMetrics;
import com.carpet.rof.rules.oec.OecQueryFrame;
import com.carpet.rof.rules.oec.OecSectionAccess;
import com.carpet.rof.rules.oec.SectionEntityGrid;

import java.util.ArrayList;
import java.util.BitSet;

public final class LithiumPushCollector {
    private LithiumPushCollector() {}

    /** Returns false only when the caller must invoke Lithium's original collector. */
    public static boolean tryCollect(Object section, long gameTime, Entity except, AABB box,
                                     EntityPushablePredicate<? super Entity> predicate, ArrayList<Entity> output) {
        if (!(section instanceof OecSectionAccess sectionAccess) || !LithiumSectionBridge.isAvailable(section)) return false;
        SectionEntityGrid grid = sectionAccess.rof$prepareGrid(gameTime);
        if (grid == null || !grid.isValid()) return false;

        ReferenceMaskedList<Object> masked = LithiumSectionBridge.getPushableEntities(section);
        if (!LithiumSectionBridge.isAvailable(section)) return false;
        LithiumMaskedListAccess maskAccess = null;
        if (masked != null) {
            if (!(masked instanceof LithiumMaskedListAccess access)) return false;
            maskAccess = access;
        }

        int intersecting = 0;
        int accepted = 0;
        try (OecQueryFrame frame = OecQueryFrame.acquire()) {
            if (!grid.collectCandidateSlots(box, frame)) return false;
            BitSet candidates = frame.bits();
            OecMetrics.GRID_QUERIES.increment();
            OecMetrics.CANDIDATES.add(candidates.cardinality());

            for (int slot = candidates.nextSetBit(0); slot >= 0; slot = candidates.nextSetBit(slot + 1)) {
                Entity entity = grid.entity(slot);
                if (entity == null || (maskAccess != null && !maskAccess.rof$isVisible(entity))) continue;
                if (!grid.intersects(slot, box) || entity.isSpectator() || entity == except || entity instanceof EnderDragon) continue;
                intersecting++;
                OecMetrics.EXACT_HITS.increment();
                OecMetrics.PREDICATE_CALLS.increment();
                if (predicate.test(entity)) {
                    accepted++;
                    output.add(entity);
                }
            }
        }

        if (masked == null && intersecting >= 25 && intersecting >= accepted * 2
                && LithiumSectionBridge.getPushableEntities(section) == null) {
            LithiumSectionBridge.startFiltering(section);
        }
        return true;
    }
}
