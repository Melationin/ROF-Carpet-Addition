package com.carpet.rof.rules.asyncWorldgen.randomTick;

import com.carpet.rof.rules.asyncWorldgen.AsyncSettings;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.rules.asyncWorldgen.DebugStats;
import com.carpet.rof.rules.asyncWorldgen.AsyncExecutor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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
        if (!AsyncSettings.asyncRandomTick || BATCH.isEmpty()) {
            BATCH.clear();
            return;
        }
        List<Work> work = List.copyOf(BATCH);
        BATCH.clear();
        DebugStats.Recording stats = ROFTool.DEBUG ? DebugStats.current() : null;
        AsyncExecutor.submitRandomTick(() -> work.forEach(item -> compute(item.chunk(), epoch, item.speed(), stats)));
    }

    //使用原版选区逻辑：对每个随机刻区块段随机抽取 speed 个坐标，命中可随机刻的方块或流体则记录，
    //实际的 randomTick 执行仍在主线程 consume 中完成，与原版 tickChunk 的命中分布一致。
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
                int baseY = minY + (i << 4);
                for (int attempt = 0; attempt < speed; attempt++) {
                    //单次 nextInt 拆包出 x/y/z（各取 4 位）
                    int value = random.nextInt();
                    int x = value & 15;
                    int y = value >>> 8 & 15;
                    int z = value >>> 4 & 15;
                    BlockState state = section.getBlockState(x, y, z);

                    if (state.isRandomlyTicking() || state.getFluidState().isRandomlyTicking()) {
                        if (state.getFluidState().getType().isSame(Fluids.LAVA)
                                && !level.canSpreadFireAround(new BlockPos(minX + x, baseY + y, minZ + z))) {
                            continue;
                        }
                        positions.add(((baseY + y) << 16) | (z << 8) | x);
                    }
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

    //执行预计算出的随机刻：方块与流体分别判定并 tick，与原版 tickChunk 行为一致。
    public static boolean consume(ServerLevel level, LevelChunk chunk)
    {
        RandomTickResult result = ((AsyncRandomTickChunk) chunk).rof$getAsyncRandomTickResult();
        boolean usedAsync = AsyncSettings.asyncRandomTick && result != null && result.epoch() == level.getServer().getTickCount();
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
