package com.carpet.rof.entity.enderPearl;

import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public final class PearlEntityRaycaster
{
    private PearlEntityRaycaster() {}

    public static @Nullable EntityHitResult getEntityHitResult(
            ServerLevel serverLevel,
            Entity source,
            Vec3 from,
            Vec3 to,
            AABB searchBox,
            Predicate<Entity> filter,
            float margin)
    {
        PearlEntitySectionCache cache = ExtraWorldDatas.fromWorld(serverLevel).pearlEntitySectionCache;
        if (!cache.isCacheValid(serverLevel)) {
            PearlEntitySectionCache.ensureCachesValid(serverLevel.getServer());
        }

        EntitySectionStorage<Entity> storage = serverLevel.entityManager.sectionStorage;
        HitState state = new HitState();

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double lenSqr = dx * dx + dy * dy + dz * dz;

        if (lenSqr < 1.0E-12) {
            int minSx = SectionPos.posToSectionCoord(from.x - 2.0);
            int maxSx = SectionPos.posToSectionCoord(from.x + 2.0);
            int minSy = SectionPos.posToSectionCoord(from.y - 2.5);
            int maxSy = SectionPos.posToSectionCoord(from.y + 1.0);
            int minSz = SectionPos.posToSectionCoord(from.z - 2.0);
            int maxSz = SectionPos.posToSectionCoord(from.z + 2.0);

            for (int ny = minSy; ny <= maxSy; ny++) {
                for (int nz = minSz; nz <= maxSz; nz++) {
                    for (int nx = minSx; nx <= maxSx; nx++) {
                        testSection(serverLevel, cache, storage, SectionPos.asLong(nx, ny, nz),
                                source, from, to, searchBox, filter, margin, state);
                    }
                }
            }
        } else {
            traceSections3dDda(serverLevel, cache, storage, source, from, to, dx, dy, dz,
                    Math.sqrt(lenSqr), searchBox, filter, margin, state);
        }

        if (!serverLevel.dragonParts().isEmpty()) {
            for (Entity part : serverLevel.dragonParts()) {
                state.testCandidate(part, source, from, to, searchBox, filter, margin);
            }
        }

        return state.bestEntity == null ? null : new EntityHitResult(state.bestEntity, state.bestLoc);
    }

    private static void traceSections3dDda(
            ServerLevel serverLevel,
            PearlEntitySectionCache cache,
            EntitySectionStorage<Entity> storage,
            Entity source,
            Vec3 from,
            Vec3 to,
            double dx,
            double dy,
            double dz,
            double totalLength,
            AABB searchBox,
            Predicate<Entity> filter,
            float margin,
            HitState state)
    {
        int sx = SectionPos.posToSectionCoord(from.x);
        int sy = SectionPos.posToSectionCoord(from.y);
        int sz = SectionPos.posToSectionCoord(from.z);

        int endSx = SectionPos.posToSectionCoord(to.x);
        int endSy = SectionPos.posToSectionCoord(to.y);
        int endSz = SectionPos.posToSectionCoord(to.z);

        int stepX;
        double tDeltaX;
        double tMaxX;
        if (dx > 0.0) {
            stepX = 1;
            tDeltaX = 16.0 / dx;
            tMaxX = ((sx + 1) * 16.0 - from.x) / dx;
        } else if (dx < 0.0) {
            stepX = -1;
            tDeltaX = -16.0 / dx;
            tMaxX = (sx * 16.0 - from.x) / dx;
        } else {
            stepX = 0;
            tDeltaX = Double.POSITIVE_INFINITY;
            tMaxX = Double.POSITIVE_INFINITY;
        }

        int stepY;
        double tDeltaY;
        double tMaxY;
        if (dy > 0.0) {
            stepY = 1;
            tDeltaY = 16.0 / dy;
            tMaxY = ((sy + 1) * 16.0 - from.y) / dy;
        } else if (dy < 0.0) {
            stepY = -1;
            tDeltaY = -16.0 / dy;
            tMaxY = (sy * 16.0 - from.y) / dy;
        } else {
            stepY = 0;
            tDeltaY = Double.POSITIVE_INFINITY;
            tMaxY = Double.POSITIVE_INFINITY;
        }

        int stepZ;
        double tDeltaZ;
        double tMaxZ;
        if (dz > 0.0) {
            stepZ = 1;
            tDeltaZ = 16.0 / dz;
            tMaxZ = ((sz + 1) * 16.0 - from.z) / dz;
        } else if (dz < 0.0) {
            stepZ = -1;
            tDeltaZ = -16.0 / dz;
            tMaxZ = (sz * 16.0 - from.z) / dz;
        } else {
            stepZ = 0;
            tDeltaZ = Double.POSITIVE_INFINITY;
            tMaxZ = Double.POSITIVE_INFINITY;
        }

        LongOpenHashSet visitedSections = new LongOpenHashSet();
        double tEnter = 0.0;

        while (true) {
            if (state.bestEntity != null) {
                double enterDist = tEnter * totalLength - 3.0;
                if (enterDist > 0.0 && enterDist * enterDist > state.bestDistSqr) {
                    break;
                }
            }

            double tNext = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));
            double tExit = Math.min(tNext, 1.0);

            double x0 = from.x + dx * tEnter;
            double y0 = from.y + dy * tEnter;
            double z0 = from.z + dz * tEnter;
            double x1 = from.x + dx * tExit;
            double y1 = from.y + dy * tExit;
            double z1 = from.z + dz * tExit;

            int minSx = Math.max(sx - 1, SectionPos.posToSectionCoord(Math.min(x0, x1) - 2.0));
            int maxSx = Math.min(sx + 1, SectionPos.posToSectionCoord(Math.max(x0, x1) + 2.0));
            int minSy = Math.max(sy - 1, SectionPos.posToSectionCoord(Math.min(y0, y1) - 2.5));
            int maxSy = Math.min(sy + 1, SectionPos.posToSectionCoord(Math.max(y0, y1) + 1.0));
            int minSz = Math.max(sz - 1, SectionPos.posToSectionCoord(Math.min(z0, z1) - 2.0));
            int maxSz = Math.min(sz + 1, SectionPos.posToSectionCoord(Math.max(z0, z1) + 2.0));

            for (int ny = minSy; ny <= maxSy; ny++) {
                for (int nz = minSz; nz <= maxSz; nz++) {
                    for (int nx = minSx; nx <= maxSx; nx++) {
                        long sectionKey = SectionPos.asLong(nx, ny, nz);
                        if (visitedSections.add(sectionKey)) {
                            testSection(serverLevel, cache, storage, sectionKey,
                                    source, from, to, searchBox, filter, margin, state);
                        }
                    }
                }
            }

            if (state.bestEntity != null) {
                double exitDist = tExit * totalLength;
                if (state.bestDistSqr <= exitDist * exitDist) {
                    break;
                }
            }

            if (tNext >= 1.0 || (sx == endSx && sy == endSy && sz == endSz)) {
                break;
            }

            tEnter = tExit;
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                sx += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
                sy += stepY;
                tMaxY += tDeltaY;
            } else {
                sz += stepZ;
                tMaxZ += tDeltaZ;
            }
        }
    }

    private static void testSection(
            ServerLevel serverLevel,
            PearlEntitySectionCache cache,
            EntitySectionStorage<Entity> storage,
            long sectionKey,
            Entity source,
            Vec3 from,
            Vec3 to,
            AABB searchBox,
            Predicate<Entity> filter,
            float margin,
            HitState state)
    {
        if (cache.contains(sectionKey)) {
            cache.ensureTargetSectionLoaded(serverLevel, sectionKey);
        }
        EntitySection<Entity> section = storage.getSection(sectionKey);
        if (section == null || section.isEmpty() || !section.getStatus().isAccessible()) {
            return;
        }
        section.getEntities().forEach(candidate ->
                state.testCandidate(candidate, source, from, to, searchBox, filter, margin));
    }

    private static final class HitState
    {
        double bestDistSqr = Double.MAX_VALUE;
        @Nullable Entity bestEntity;
        @Nullable Vec3 bestLoc;

        void testCandidate(
                Entity candidate,
                Entity source,
                Vec3 from,
                Vec3 to,
                AABB searchBox,
                Predicate<Entity> filter,
                float margin)
        {
            if (candidate == source || !candidate.getBoundingBox().intersects(searchBox) || !filter.test(candidate)) {
                return;
            }
            AABB targetBox = candidate.getBoundingBox().inflate(margin);
            Optional<Vec3> hit = targetBox.clip(from, to);
            if (hit.isPresent()) {
                Vec3 hitPos = hit.get();
                double distSqr = from.distanceToSqr(hitPos);
                if (distSqr < this.bestDistSqr) {
                    this.bestDistSqr = distSqr;
                    this.bestEntity = candidate;
                    this.bestLoc = hitPos;
                }
            }
        }
    }
}
