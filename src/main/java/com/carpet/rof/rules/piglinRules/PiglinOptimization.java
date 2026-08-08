package com.carpet.rof.rules.piglinRules;

import com.carpet.rof.utils.ROFTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.HashMap;

import static com.carpet.rof.rules.piglinRules.PiglinRulesSettings.piglinStackingAISuppression;

public final class PiglinOptimization {
    static final long GROUP_REFRESH_INTERVAL = 400L;
    static final int INACTIVE_BRAIN_INTERVAL = 5;
    private static final Map<ServerLevel, Map<Long, SharedItemSensorResult>> SHARED_ITEM_SENSOR_CACHE = new WeakHashMap<>();

    private PiglinOptimization() {
    }

    public static void refreshGroupIfNeeded(ServerLevel world, Piglin piglin) {
        PiglinEntityAccessor accessor = (PiglinEntityAccessor) piglin;
        long gameTime = world.getGameTime();
        if (gameTime < accessor.rof$getNextGroupRefreshTick()) {
            return;
        }

        List<Piglin> group = world.getEntities(
                EntityTypes.PIGLIN,
                new AABB(piglin.blockPosition()),
                Piglin::isAlive
        );
        Set<Integer> activeEntityIds = selectRegularAiIds(
                group.stream().map(Piglin::getId).toList(),
                piglinStackingAISuppression
        );
        long nextRefreshTick = gameTime + GROUP_REFRESH_INTERVAL;
        for (Piglin member : group) {
            ((PiglinEntityAccessor) member).rof$setOptimizationState(
                    activeEntityIds.contains(member.getId()),
                    nextRefreshTick
            );
        }
    }

    static int regularAiLimit(int groupSize, int configuredLimit) {
        if (groupSize <= 0) {
            return 0;
        }
        return Math.min(groupSize, Math.max(1, configuredLimit));
    }

    static Set<Integer> selectRegularAiIds(Collection<Integer> entityIds, int configuredLimit) {
        List<Integer> stableOrder = new ArrayList<>(entityIds);
        stableOrder.sort((first, second) -> {
            int hashComparison = Integer.compare(ROFTool.fastHash(first), ROFTool.fastHash(second));
            return hashComparison != 0 ? hashComparison : Integer.compare(first, second);
        });

        int activeLimit = regularAiLimit(stableOrder.size(), configuredLimit);
        return new HashSet<>(stableOrder.subList(0, activeLimit));
    }

    public static boolean shouldRunRegularAi(Piglin piglin) {
        return ((PiglinEntityAccessor) piglin).rof$isRegularAiActive();
    }

    public static boolean shouldUseVanillaMovement(Piglin piglin) {
        return shouldRunRegularAi(piglin);
    }

    public static boolean shouldTickBrainBehaviors(Piglin piglin, long gameTime) {
        return shouldRunRegularAi(piglin)
                || shouldTickInactiveBrain(piglin.getId(), gameTime);
    }

    static boolean shouldTickInactiveBrain(int entityId, long gameTime) {
        return Math.floorMod(gameTime + ROFTool.fastHash(entityId), INACTIVE_BRAIN_INTERVAL) == 0;
    }

    public static void tickSharedNearestItemSensor(Sensor<? super Piglin> sensor, ServerLevel world, Piglin piglin) {
        if (shouldRunRegularAi(piglin)) {
            sensor.tick(world, piglin);
            return;
        }

        if (piglin.getOffhandItem().getItem() == PiglinAi.BARTERING_ITEM) {
            return;
        }

        long gameTime = world.getGameTime();
        long groupKey = piglin.blockPosition().asLong();
        Map<Long, SharedItemSensorResult> worldCache = SHARED_ITEM_SENSOR_CACHE.computeIfAbsent(
                world,
                ignored -> new HashMap<>()
        );
        SharedItemSensorResult cached = worldCache.get(groupKey);
        if (cached == null || cached.gameTime() != gameTime) {
            sensor.tick(world, piglin);
            ItemEntity barterItem = piglin.getBrain()
                    .getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)
                    .filter(ItemEntity::isAlive)
                    .filter(item -> item.getItem().getItem() == PiglinAi.BARTERING_ITEM)
                    .orElse(null);
            cached = new SharedItemSensorResult(gameTime, barterItem);
            worldCache.put(groupKey, cached);
            pruneSharedItemCache(worldCache, gameTime);
        }

        ItemEntity barterItem = cached.barterItem();
        if (barterItem != null && barterItem.isAlive()) {
            piglin.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, barterItem);
        } else {
            piglin.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        }
    }

    private static void pruneSharedItemCache(Map<Long, SharedItemSensorResult> worldCache, long gameTime) {
        if (worldCache.size() > 1024) {
            worldCache.entrySet().removeIf(entry -> entry.getValue().gameTime() < gameTime - 40L);
        }
    }

    private record SharedItemSensorResult(long gameTime, ItemEntity barterItem) {
    }
}
