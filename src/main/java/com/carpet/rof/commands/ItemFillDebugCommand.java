package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.debug.ItemFillStats;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@ROFCommand
public final class ItemFillDebugCommand
{
    private ItemFillDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug itemFill").command(context -> stats(context.getSource()));
    }

    private static int stats(CommandSourceStack source)
    {
        for (String line : ItemFillStats.snapshot(source.getServer()).lines())
        {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }
}
