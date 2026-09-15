package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.rules.oec.OecMetrics;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@ROFCommand
public final class OecDebugCommand {
    private OecDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug oec").command(context -> stats(context.getSource()));
        helper.registerCommand("rofDebug oec reset").command(context -> reset(context.getSource()));
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
