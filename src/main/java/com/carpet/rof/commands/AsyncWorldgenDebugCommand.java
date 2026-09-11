package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.rules.asyncWorldgen.DebugStats;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/** Development-environment diagnostics; intentionally absent from production command trees. */
@ROFCommand
public final class AsyncWorldgenDebugCommand {
    private AsyncWorldgenDebugCommand() { }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!ROFTool.DEBUG) return;
        dispatcher.register(Commands.literal("rofDebug")
                .then(Commands.literal("asyncWorldgen").executes(context -> start(context.getSource()))));
    }

    private static int start(CommandSourceStack source) {
        if (!DebugStats.start()) {
            source.sendFailure(Component.literal("ROF async-worldgen debug recording is already running."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Recording async random-tick and natural-spawn statistics for " + DebugStats.DURATION_TICKS + " gt."), false);
        ROFEvents.ServerTickEndTasks.register((server, tick) -> {
            if (tick + 1 < DebugStats.DURATION_TICKS) return false;
            DebugStats.Snapshot result = DebugStats.stop();
            if (result != null) sendResult(source, result);
            return true;
        });
        return 1;
    }

    private static void sendResult(CommandSourceStack source, DebugStats.Snapshot result) {
        source.sendSuccess(() -> Component.literal("--- ROF async worldgen debug (200 gt) ---"), false);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                "Random tick: async used %d/%d (%.2f%%); returned %d results from %d chunks (%.2f results/chunk).",
                result.randomTickAsyncUsed(), result.randomTickDecisions(), result.randomTickAsyncRatio() * 100.0,
                result.randomTickReturnedResults(), result.randomTickReturnedChunks(), result.randomTickResultsPerChunk())), false);
        source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT,
                "Natural spawn: async used %d/%d (%.2f%%); returned %d attempts from %d chunks (%.2f attempts/chunk).",
                result.naturalSpawnAsyncUsed(), result.naturalSpawnDecisions(), result.naturalSpawnAsyncRatio() * 100.0,
                result.naturalSpawnReturnedAttempts(), result.naturalSpawnReturnedChunks(), result.naturalSpawnAttemptsPerChunk())), false);
    }
}
