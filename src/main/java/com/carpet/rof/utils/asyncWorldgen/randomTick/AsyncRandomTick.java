package com.carpet.rof.utils.asyncWorldgen.randomTick;

import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.asyncWorldgen.DebugStats;
import com.carpet.rof.utils.asyncWorldgen.AsyncExecutor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.caffeinemc.mods.lithium.common.block.BlockCountingSection;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlagHolder;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.caffeinemc.mods.lithium.common.world.section.LithiumSectionData;
import net.caffeinemc.mods.lithium.common.world.section.RandomTickingSectionDataHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;


public final class AsyncRandomTick
{
    private static final List<Work> BATCH = new ArrayList<>(); // server thread only
    private static final ThreadLocal<RandomSource> WORKER_RANDOM = ThreadLocal.withInitial(
            RandomSource::createThreadLocalInstance);
    private static final RandomSource SERVER_RANDOM = RandomSource.createThreadLocalInstance();
    private static volatile int currentEpoch = -1;

    private AsyncRandomTick()
    {
    }

    public static void collect(LevelChunk chunk, int speed)
    {
        if (AsyncSettings.asyncRandomTick && speed > 0)
            BATCH.add(new Work(chunk, speed));
    }

    public static void submitBatch(MinecraftServer server)
    {
        int epoch = server.getTickCount() + 1;
        currentEpoch = epoch;
        if (!AsyncSettings.asyncRandomTick || BATCH.isEmpty()) {
            BATCH.clear();
            return;
        }
        List<Work> work = List.copyOf(BATCH);
        BATCH.clear();
        DebugStats.Recording stats = ROFTool.DEBUG ? DebugStats.current() : null;
        AsyncExecutor.submitRandomTick(() -> work.forEach(item -> compute(item.chunk(), epoch, item.speed(), stats)));
    }

    //部分参考了锂的逻辑
    private static void compute(LevelChunk chunk, int epoch, int speed, DebugStats.Recording stats)
    {
        try {
            IntArrayList positions = new IntArrayList();
            ServerLevel level = (ServerLevel) chunk.getLevel();
            RandomSource random = WORKER_RANDOM.get();
            LevelChunkSection[] sections = chunk.getSections();
            int minY = chunk.getMinY(), minX = chunk.getPos().getMinBlockX(), minZ = chunk.getPos().getMinBlockZ();
            int precipitation = 0;
            for (int i = 0; i < speed; i++)
                if (random.nextInt(48) == 0)
                    precipitation++;
            for (int i = 0; i < sections.length; i++) {
                LevelChunkSection section = sections[i];
                if (section == null || !section.isRandomlyTicking())
                    continue;
                int count = ((BlockCountingSection) section).lithium$getCount(BlockStateFlags.RANDOM_TICKING);
                byte[] index = ((LithiumSectionData) section).lithium$getSectionData().getRandomTickableBlocksByY();
                if (count <= 0 || index == null)
                    continue;
                for (int attempt = 0; attempt < speed; attempt++) {
                    int blockIndex = randomIndex(random);
                    if (blockIndex >= count)
                        continue;
                    int packed = find(level, section, blockIndex, index, minX, minY + (i << 4), minZ);
                    if (packed >= 0)
                        positions.add(packed);
                }
            }
            ((AsyncRandomTickChunk) chunk).rof$setAsyncRandomTickResult(
                    new RandomTickResult(epoch, precipitation, positions.toIntArray()));
            if (ROFTool.DEBUG && stats != null)
                stats.recordRandomTickResult(positions.size());
        } catch (Throwable error) {
            if (ROFTool.DEBUG) {
                error.printStackTrace();
            }
        }
    }

    private static int randomIndex(RandomSource random)
    {
        int value = random.nextInt();
        return (value & 15) | (value >>> 8 & 0xf00) | (value >>> 4 & 0xf0);
    }

    //移植自锂
    private static int find(ServerLevel level, LevelChunkSection section, int index, byte[] data, int minX, int baseY, int minZ)
    {
        int mini = 0;
        for (; mini < data.length; mini++) {
            int count = Byte.toUnsignedInt(data[mini]);
            if (index < count)
                break;
            index -= count;
        }
        for (int packed = mini * RandomTickingSectionDataHelper.MINISECTION_SIZE; packed < 4096; packed++) {
            int x = packed & 15, y = packed >> 8 & 15, z = packed >> 4 & 15;
            BlockState state = section.getBlockState(x, y, z);
            if ((((BlockStateFlagHolder) state).lithium$getAllFlags() & RandomTickingSectionDataHelper.RANDOM_TICKING_FLAG_MASK) == 0)
                continue;
            if (index-- != 0)
                continue;
            if (state.getFluidState().getType().isSame(Fluids.LAVA) && !level.canSpreadFireAround(
                    new BlockPos(minX + x, baseY + y, minZ + z)))
                return -1;
            return ((baseY + y) << 16) | (z << 8) | x;
        }
        return -1;
    }

    //部分参考了锂的逻辑
    public static boolean consume(ServerLevel level, LevelChunk chunk)
    {
        RandomTickResult result = ((AsyncRandomTickChunk) chunk).rof$getAsyncRandomTickResult();
        boolean usedAsync = AsyncSettings.asyncRandomTick && result != null && result.epoch() == currentEpoch;
        if (ROFTool.DEBUG)
            DebugStats.recordRandomTickDecision(usedAsync);
        if (!usedAsync)
            return false;
        int minX = chunk.getPos().getMinBlockX(), minZ = chunk.getPos().getMinBlockZ();
        for (int i = 0; i < result.precipitationCount(); i++)
            level.tickPrecipitation(level.getBlockRandomPos(minX, 0, minZ, 15));
        LevelChunkSection[] sections = chunk.getSections();
        int minY = chunk.getMinY();
        for (int packed : result.positions()) {
            int x = packed & 255, z = packed >>> 8 & 255, y = packed >> 16;
            int sectionIndex = (y - minY) >> 4;
            if (sectionIndex < 0 || sectionIndex >= sections.length)
                continue;
            BlockState state = sections[sectionIndex].getBlockState(x, y & 15, z);
            BlockPos pos = new BlockPos(minX | x, y, minZ | z);
            if (state.isRandomlyTicking())
                state.randomTick(level, pos, SERVER_RANDOM);
            FluidState fluid = state.getFluidState();
            if (fluid.isRandomlyTicking())
                fluid.randomTick(level, pos, SERVER_RANDOM);
        }
        return true;
    }

    private record Work(LevelChunk chunk, int speed)
    {
    }
}
