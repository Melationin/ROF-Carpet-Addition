package com.carpet.rof.commands;

import com.carpet.rof.annotation.CarpetCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

@CarpetCommand
public class BaseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

    }
}
