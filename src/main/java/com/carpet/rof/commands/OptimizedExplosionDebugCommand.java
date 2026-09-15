package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.debug.OptimizedExplosionStats;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@ROFCommand
public final class OptimizedExplosionDebugCommand
{
    private OptimizedExplosionDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug optimizedExplosion").command(context -> report(context.getSource()));
        helper.registerCommand("rofDebug optimizedExplosion start").command(context -> start(context.getSource()));
        helper.registerCommand("rofDebug optimizedExplosion stop").command(context -> stop(context.getSource()));
        helper.registerCommand("rofDebug optimizedExplosion reset").command(context -> reset(context.getSource()));
    }

    private static int start(CommandSourceStack source)
    {
        OptimizedExplosionStats.start(source.getServer().getTickCount());
        return send(source, "ROF optimizedExplosion: start recording");
    }

    private static int stop(CommandSourceStack source)
    {
        OptimizedExplosionStats.stop();
        return report(source);
    }

    private static int reset(CommandSourceStack source)
    {
        OptimizedExplosionStats.reset();
        return send(source, "ROF optimizedExplosion: counters cleared");
    }

    private static int report(CommandSourceStack source)
    {
        for (String line : OptimizedExplosionStats.lines(source.getServer().getTickCount()))
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
