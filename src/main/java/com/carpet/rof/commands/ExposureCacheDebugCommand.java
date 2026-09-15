package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@ROFCommand
public final class ExposureCacheDebugCommand
{
    private ExposureCacheDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug exposureCache").command(context -> report(context.getSource()));
        helper.registerCommand("rofDebug exposureCache start").command(context -> start(context.getSource()));
        helper.registerCommand("rofDebug exposureCache stop").command(context -> stop(context.getSource()));
        helper.registerCommand("rofDebug exposureCache reset").command(context -> reset(context.getSource()));
    }

    private static int start(CommandSourceStack source)
    {
        OptimizedExplosionStats.start(source.getServer().getTickCount());
        return send(source, "ROF exposure cache: recording started (counters shared with /rofDebug optimizedExplosion)");
    }

    private static int stop(CommandSourceStack source)
    {
        OptimizedExplosionStats.stop();
        return report(source);
    }

    private static int reset(CommandSourceStack source)
    {
        OptimizedExplosionStats.reset();
        return send(source, "ROF exposure cache: counters cleared");
    }

    private static int report(CommandSourceStack source)
    {
        for (String line : OptimizedExplosionStats.exposureLines(source.getServer().getTickCount()))
        {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }

    private static int send(CommandSourceStack source, String line)
    {
        source.sendSuccess(() -> Component.literal(line), false);
        return 1;
    }
}
