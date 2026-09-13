package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collections;

@ROFCommand
public final class RepeatDebugCommand
{
    private RepeatDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        if (!ROFTool.DEBUG) return;
        dispatcher.register(Commands.literal("rofDebug")
                .then(Commands.literal("repeat")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                .then(Commands.literal("run")
                                        .fork(dispatcher.getRoot(), context ->
                                                Collections.nCopies(IntegerArgumentType.getInteger(context, "count"), context.getSource()))))));
    }
}
