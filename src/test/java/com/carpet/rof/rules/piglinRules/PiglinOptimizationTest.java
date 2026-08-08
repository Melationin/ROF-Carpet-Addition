package com.carpet.rof.rules.piglinRules;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiglinOptimizationTest {
    @Test
    void emptyGroupHasNoRegularAi() {
        assertEquals(0, PiglinOptimization.regularAiLimit(0, 100));
        assertEquals(Set.of(), PiglinOptimization.selectRegularAiIds(List.of(), 100));
    }

    @Test
    void configuredLimitIsAnExactCap() {
        Set<Integer> selected = PiglinOptimization.selectRegularAiIds(
                List.of(91, 12, 77, 34, 55),
                2
        );

        assertEquals(2, selected.size());
        assertEquals(selected, PiglinOptimization.selectRegularAiIds(
                List.of(55, 34, 77, 12, 91),
                2
        ));
    }

    @Test
    void everySmallGroupRemainsActive() {
        Set<Integer> ids = Set.of(3, 7, 11);
        assertEquals(ids, PiglinOptimization.selectRegularAiIds(ids, 100));
    }

    @Test
    void nonPositiveLimitStillKeepsOneRepresentative() {
        assertEquals(1, PiglinOptimization.selectRegularAiIds(List.of(3, 7, 11), 0).size());
        assertEquals(1, PiglinOptimization.selectRegularAiIds(List.of(3, 7, 11), -5).size());
    }

    @Test
    void inactiveBrainRunsOncePerInterval() {
        for (int entityId : List.of(1, 42, 270, Integer.MAX_VALUE)) {
            int runs = 0;
            for (long tick = 10_000; tick < 10_000 + PiglinOptimization.INACTIVE_BRAIN_INTERVAL; tick++) {
                if (PiglinOptimization.shouldTickInactiveBrain(entityId, tick)) {
                    runs++;
                }
            }
            assertEquals(1, runs);
        }
    }
}
