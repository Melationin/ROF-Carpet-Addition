package com.carpet.rof.utils.asyncWorldgen.spawner;

import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.asyncWorldgen.DebugStats;
import com.carpet.rof.utils.asyncWorldgen.AsyncExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class AsyncNaturalSpawner
{
    private static final List<Work> BATCH = new ArrayList<>();
    private static final ThreadLocal<RandomSource> WORKER_RANDOM = ThreadLocal.withInitial(
            RandomSource::createThreadLocalInstance);

    private AsyncNaturalSpawner()
    {
    }

    public static boolean consume(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state, List<MobCategory> categories)
    {
        SpawnResult result = ((AsyncNaturalSpawnerChunk) chunk).rof$getAsyncSpawnResult();
        boolean consumed = AsyncSettings.asyncNaturalSpawning && result != null && result.epoch() == level.getServer()
                .getTickCount();
        if (ROFTool.DEBUG)
            DebugStats.recordNaturalSpawnDecision(consumed);
        if (consumed)
            consumePlan(level, chunk, state, categories, result.candidates());
        if (AsyncSettings.asyncNaturalSpawning && !categories.isEmpty())
            BATCH.add(new Work(chunk, List.copyOf(categories)));
        return consumed;
    }

    public static void submitBatch(MinecraftServer server)
    {
        int epoch = server.getTickCount() + 1;
        if (!AsyncSettings.asyncNaturalSpawning || BATCH.isEmpty()) {
            BATCH.clear();
            return;
        }
        List<Work> work = List.copyOf(BATCH);
        BATCH.clear();
        DebugStats.Recording stats = ROFTool.DEBUG ? DebugStats.current() : null;
        AsyncExecutor.submitSpawning(
                () -> work.forEach(item -> compute(item.chunk(), epoch, item.categories(), stats)));
    }

    private static void consumePlan(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state, List<MobCategory> categories, List<SpawnCandidate> candidates)
    {
        if (candidates.isEmpty())
            return;
        StructureManager structureManager = level.structureManager();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        RandomSource random = level.getRandom();
        // Candidates already follow vanilla category -> group -> attempt order.
        // Consume once rather than rescanning the complete list for each group.
        MobCategory currentCategory = null;
        boolean canSpawnCategory = false;
        boolean stopCategory = false;
        int clusterSize = 0;

        int currentGroup = -1;
        int groupSize = 0;
        boolean skipGroup = false;
        SpawnGroupData spawnGroupData = null;

        for (SpawnCandidate candidate : candidates) {
            MobCategory category = candidate.category();
            if (!categories.contains(category))
                continue;
            if (category != currentCategory) {
                currentCategory = category;
                canSpawnCategory = state.canSpawnForCategoryLocal(category, chunk.getPos());
                stopCategory = false;
                clusterSize = 0;
                currentGroup = -1;
            }
            if (!canSpawnCategory || stopCategory)
                continue;
            if (currentGroup != candidate.groupIndex()) {
                currentGroup = candidate.groupIndex();
                groupSize = 0;
                skipGroup = false;
                spawnGroupData = null;
            }
            if (skipGroup)
                continue;

            BlockPos pos = candidate.pos();
            MobSpawnSettings.SpawnerData spawnData = candidate.spawnData();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
            double x = pos.getX() + .5, z = pos.getZ() + .5;
            Player player = level.getNearestPlayer(x, pos.getY(), z, -1.0, false);
            double distance = player == null ? Double.MAX_VALUE : player.distanceToSqr(x, pos.getY(), z);
            if (candidate.staticChecksPassed()) {
                if (!remainingPositionChecks(level, spawnData, mutable, candidate.placementChecksPassed()))
                    continue;
            } else if (!NaturalSpawner.isValidSpawnPostitionForType(level, category, structureManager, generator,
                    spawnData, mutable, distance))
                continue;
            if (!state.canSpawn(spawnData.type(), mutable, chunk))
                continue;
            Mob mob = NaturalSpawner.getMobForSpawn(level, spawnData.type());
            if (mob == null) {
                stopCategory = true;
                continue;
            }
            mob.snapTo(x, pos.getY(), z, random.nextFloat() * 360.0F, 0.0F);
            if (!NaturalSpawner.isValidPositionForMob(level, mob, distance))
                continue;
            spawnGroupData = mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()),
                    EntitySpawnReason.NATURAL, spawnGroupData);
            clusterSize++;
            groupSize++;
            level.addFreshEntityWithPassengers(mob);
            state.afterSpawn(mob, chunk);
            if (clusterSize >= mob.getMaxSpawnClusterSize())
                stopCategory = true;
            else if (mob.isMaxGroupSizeReached(groupSize))
                skipGroup = true;
        }
    }

    private static boolean remainingPositionChecks(ServerLevel level, MobSpawnSettings.SpawnerData data, BlockPos pos, boolean placementPassed)
    {
        if (!placementPassed && !SpawnPlacements.isSpawnPositionOk(data.type(), level, pos))
            return false;
        return SpawnPlacements.checkSpawnRules(data.type(), level, EntitySpawnReason.NATURAL, pos,
                level.getRandom()) && level.noCollision(
                data.type().getSpawnAABB(pos.getX() + .5, pos.getY(), pos.getZ() + .5));
    }

    private static void compute(LevelChunk chunk, int epoch, List<MobCategory> categories, DebugStats.Recording stats)
    {
        try {
            List<SpawnCandidate> out = new ArrayList<>();
            ServerLevel level = (ServerLevel) chunk.getLevel();
            RandomSource random = WORKER_RANDOM.get();
            for (MobCategory category : categories) {
                int sx = chunk.getPos().getMinBlockX() + random.nextInt(16);
                int sz = chunk.getPos().getMinBlockZ() + random.nextInt(16);
                int topY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, sx, sz) + 1;
                int y = Mth.randomBetweenInclusive(random, level.getMinY(), topY);
                BlockPos start = new BlockPos(sx, y, sz);
                if (start.getY() < level.getMinY() + 1 || chunk.getBlockState(start).isRedstoneConductor(chunk, start))
                    continue;
                for (int group = 0; group < 3; group++) {
                    int x = start.getX(), z = start.getZ();
                    MobSpawnSettings.SpawnerData current = null;
                    int max = (int) Math.ceil(random.nextFloat() * 4.0F);
                    for (int attempt = 0; attempt < max; attempt++) {
                        x += random.nextInt(6) - random.nextInt(6);
                        z += random.nextInt(6) - random.nextInt(6);
                        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
                        double xx = x + .5, zz = z + .5;
                        Player player = level.getNearestPlayer(xx, y, zz, -1.0, false);
                        if (player == null)
                            continue;
                        double distance = player.distanceToSqr(xx, y, zz);
                        if (!NaturalSpawner.isRightDistanceToPlayerAndSpawnPoint(level, chunk, pos, distance))
                            continue;
                        if (current == null) {
                            Optional<MobSpawnSettings.SpawnerData> selection = AsyncSpawnMobSelector.getRandomSpawnMobAt(
                                    level, category, random, pos);
                            if (selection.isEmpty())
                                break;
                            current = selection.get();
                            max = current.minCount() + random.nextInt(1 + current.maxCount() - current.minCount());
                        }
                        boolean staticPassed = false, placementPassed = false;
                        var type = current.type();
                        if (type.getCategory() == MobCategory.MISC)
                            continue;
                        int despawn = type.getCategory().getDespawnDistance();
                        if (!type.canSpawnFarFromPlayer() && distance > (double) despawn * despawn)
                            continue;
                        if (!type.canSummon())
                            continue;
                        AsyncSpawnMobSelector.Validation valid = AsyncSpawnMobSelector.canSpawnMobAt(level, category,
                                current, pos);
                        if (valid == AsyncSpawnMobSelector.Validation.DENIED)
                            continue;
                        if (valid != AsyncSpawnMobSelector.Validation.UNAVAILABLE) {
                            staticPassed = true;
                            AsyncSpawnPlacementValidator.Result placement = AsyncSpawnPlacementValidator.validate(level,
                                    type, pos);
                            if (placement == AsyncSpawnPlacementValidator.Result.DENIED)
                                continue;
                            if (placement == AsyncSpawnPlacementValidator.Result.ALLOWED)
                                placementPassed = true;
                        }
                        out.add(new SpawnCandidate(category, group, pos.immutable(), current, staticPassed,
                                placementPassed));
                    }
                }
            }
            ((AsyncNaturalSpawnerChunk) chunk).rof$setAsyncSpawnResult(new SpawnResult(epoch, List.copyOf(out)));
            if (stats != null)
                stats.recordNaturalSpawnResult(out.size());
        } catch (Throwable error) {
            if (ROFTool.DEBUG) {
                error.printStackTrace();
            }
        }
    }

    private record Work(LevelChunk chunk, List<MobCategory> categories)
    {
    }
}
