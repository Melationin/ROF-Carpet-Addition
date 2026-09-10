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

public final class LithiumPushCollector {
    private LithiumPushCollector() {}

    /** Returns false only when the caller must invoke Lithium's original collector. */
    public static boolean tryCollect(Object section, long gameTime, Entity except, AABB box,
                                     EntityPushablePredicate<? super Entity> predicate, ArrayList<Entity> output) {
        if (!(section instanceof OecSectionAccess sectionAccess)
                || !(section instanceof OecLithiumSectionAccess lithiumAccess)) return false;
        SectionEntityGrid grid = sectionAccess.rof$prepareGrid(gameTime);
        if (grid == null || !grid.isValid()) return false;

        ReferenceMaskedList<Entity> masked = lithiumAccess.rof$lithiumPushableEntities();
        LithiumMaskedListAccess maskAccess = null;
        if (masked != null) {
            if (!(masked instanceof LithiumMaskedListAccess access)) return false;
            maskAccess = access;
        }

        int intersecting = 0;
        int accepted = 0;
        boolean metrics = OecMetrics.ENABLED;
        try (OecQueryFrame frame = OecQueryFrame.acquire()) {
            if (!grid.collectCandidateSlots(box, frame)) return false;
            long[] words = frame.words();
            int wordCount = frame.wordCount();
            if (metrics) {
                OecMetrics.GRID_QUERIES.increment();
                OecMetrics.CANDIDATES.add(frame.cardinality());
            }

            // Inline word walk: one pass over the candidate words, no per-candidate call into BitSet.nextSetBit.
            for (int w = 0; w < wordCount; w++) {
                long word = words[w];
                while (word != 0L) {
                    int slot = (w << 6) + Long.numberOfTrailingZeros(word);
                    word &= word - 1L;
                    // Cheapest rejection first: the exact AABB test needs neither an entity dereference nor a Lithium hash lookup.
                    if (!grid.intersects(slot, box)) continue;
                    Entity entity = grid.entity(slot);
                    if (entity == null || (maskAccess != null && !maskAccess.rof$isVisible(entity))) continue;
                    if (entity.isSpectator() || entity == except || entity instanceof EnderDragon) continue;
                    intersecting++;
                    if (metrics) {
                        OecMetrics.EXACT_HITS.increment();
                        OecMetrics.PREDICATE_CALLS.increment();
                    }
                    if (predicate.test(entity)) {
                        accepted++;
                        output.add(entity);
                    }
                }
            }
        }

        if (masked == null && intersecting >= 25 && intersecting >= accepted * 2
                && lithiumAccess.rof$lithiumPushableEntities() == null) {
            lithiumAccess.rof$lithiumStartFilteringPushableEntities();
        }
        return true;
    }
}
