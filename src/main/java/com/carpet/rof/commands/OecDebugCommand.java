package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.rules.oec.OecMetrics;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

@ROFCommand
public final class OecDebugCommand {
    private OecDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!ROFTool.DEBUG) return;
        dispatcher.register(Commands.literal("rofDebug").then(Commands.literal("oec")
                .executes(context -> stats(context.getSource()))
                .then(Commands.literal("reset").executes(context -> reset(context.getSource())))));
    }

    private static int stats(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("ROF OEC: " + OecMetrics.summary()), false);
        return 1;
    }

    private static int reset(CommandSourceStack source) {
        OecMetrics.reset();
        source.sendSuccess(() -> Component.literal("ROF OEC counters reset."), false);
        return 1;
    }
}
