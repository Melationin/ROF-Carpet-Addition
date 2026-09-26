package com.carpet.rof.rules.asyncWorldgen.spawner;

import com.carpet.rof.rules.asyncWorldgen.LoadedBlockGetter;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
//? if >=1.21.5 {
import net.minecraft.util.random.WeightedList;
//?} else {
/*import net.minecraft.util.random.WeightedRandomList;
 *///?}
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;

import java.util.Map;
import java.util.Optional;
import com.carpet.rof.utils.ChunkPosHelper;


final class AsyncSpawnMobSelector
{
    private AsyncSpawnMobSelector()
    {
    }

    static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(ServerLevel level, MobCategory category, RandomSource random, BlockPos pos)
    {
        Holder<Biome> biome = biome(level, pos);
        if (biome == null)
            return Optional.empty();
        if (category == MobCategory.WATER_AMBIENT && biome.is(
                BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && random.nextFloat() < .98F)
            return Optional.empty();
        var mobs = mobsAt(level, biome, category, pos);
        return mobs == null ? Optional.empty() : mobs.getRandom(random);
    }

    static Validation canSpawnMobAt(ServerLevel level, MobCategory category, MobSpawnSettings.SpawnerData data, BlockPos pos)
    {
        Holder<Biome> biome = biome(level, pos);
        if (biome == null)
            return Validation.UNAVAILABLE;
        var mobs = mobsAt(level, biome, category, pos);
        // 1.21.5 起 WeightedList 才有 contains，1.21.4 的 WeightedRandomList 只能 unwrap 后查。
        //? if >=1.21.5 {
        return mobs == null ? Validation.UNAVAILABLE : mobs.contains(data) ? Validation.ALLOWED : Validation.DENIED;
        //?} else {
        /*return mobs == null ? Validation.UNAVAILABLE : mobs.unwrap().contains(data) ? Validation.ALLOWED : Validation.DENIED;
        *///?}
    }

    //? if >=1.21.5 {
    private static WeightedList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel level, Holder<Biome> biome, MobCategory category, BlockPos pos)
    //?} else {
    /*private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel level, Holder<Biome> biome, MobCategory category, BlockPos pos)
    *///?}
    {
        Boolean fortress = fortress(level, category, pos);
        if (fortress == null)
            return null;
        if (fortress)
            return NetherFortressStructure.FORTRESS_ENEMIES;
        ChunkAccess references = LoadedBlockGetter.findChunk(level, pos.getX() >> 4, pos.getZ() >> 4,
                ChunkStatus.STRUCTURE_REFERENCES);
        if (references == null)
            return null;
        for (Map.Entry<Structure, LongSet> entry : references.getAllReferences().entrySet()) {
            Structure structure = entry.getKey();
            StructureSpawnOverride override = structure.spawnOverrides().get(category);
            if (override == null)
                continue;
            for (long packed : entry.getValue()) {
                ChunkPos startPos = ChunkPosHelper.unpack(packed);
                ChunkAccess startChunk = LoadedBlockGetter.findChunk(level, ChunkPosHelper.x(startPos), ChunkPosHelper.z(startPos),
                        ChunkStatus.STRUCTURE_STARTS);
                if (startChunk == null)
                    return null;
                StructureStart start = startChunk.getStartForStructure(structure);
                if (start == null || !start.isValid())
                    continue;
                boolean contains = switch (override.boundingBox()) {
                    case PIECE -> level.structureManager().structureHasPieceAt(pos, start);
                    default -> start.getBoundingBox().isInside(pos);
                };
                if (contains)
                    return override.spawns();
            }
        }
        //? if >=26.3 {
        /*MobSpawnSettings settings = biome.value().getAttributes().applyModifier(EnvironmentAttributes.NATURAL_MOB_SPAWNS, MobSpawnSettings.EMPTY);
        return settings.getMobsToSpawn(category);
        *///?} else{
        MobSpawnSettings settings = biome.value().getMobSettings();
        return settings.getMobs(category);
        //?}
    }

    private static Boolean fortress(ServerLevel level, MobCategory category, BlockPos pos)
    {
        if (category != MobCategory.MONSTER)
            return false;
        BlockPos below = pos.below();
        ChunkAccess belowChunk = LoadedBlockGetter.findChunk(level, below.getX() >> 4, below.getZ() >> 4,
                ChunkStatus.FULL);
        if (belowChunk == null)
            return null;
        if (!belowChunk.getBlockState(below).is(Blocks.NETHER_BRICKS))
            return false;
        Structure fortress = level.registryAccess().lookupOrThrow(Registries.STRUCTURE)
                .getValue(BuiltinStructures.FORTRESS);
        if (fortress == null)
            return false;
        ChunkAccess references = LoadedBlockGetter.findChunk(level, pos.getX() >> 4, pos.getZ() >> 4,
                ChunkStatus.STRUCTURE_REFERENCES);
        if (references == null)
            return null;
        for (long packed : references.getReferencesForStructure(fortress)) {
            ChunkPos start = ChunkPosHelper.unpack(packed);
            ChunkAccess startChunk = LoadedBlockGetter.findChunk(level, ChunkPosHelper.x(start), ChunkPosHelper.z(start),
                    ChunkStatus.STRUCTURE_STARTS);
            if (startChunk == null)
                return null;
            StructureStart structureStart = startChunk.getStartForStructure(fortress);
            if (structureStart != null && structureStart.isValid() && structureStart.getBoundingBox().isInside(pos))
                return true;
        }
        return false;
    }

    private static Holder<Biome> biome(ServerLevel level, BlockPos pos)
    {
        ChunkAccess fallbackChunk = LoadedBlockGetter.findChunk(level, pos.getX() >> 4, pos.getZ() >> 4,
                ChunkStatus.BIOMES);
        if (fallbackChunk == null)
            return null;
        Holder<Biome> fallback = fallbackChunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
        boolean[] unavailable = {false};
        Holder<Biome> biome = level.getBiomeManager().withDifferentSource((x, y, z) ->
        {
            ChunkAccess chunk = LoadedBlockGetter.findChunk(level, x >> 2, z >> 2, ChunkStatus.BIOMES);
            if (chunk == null) {
                unavailable[0] = true;
                return fallback;
            }
            return chunk.getNoiseBiome(x, y, z);
        }).getBiome(pos);
        return unavailable[0] ? null : biome;
    }

    enum Validation
    {ALLOWED, DENIED, UNAVAILABLE}
}
