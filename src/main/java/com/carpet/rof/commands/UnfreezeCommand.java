package com.carpet.rof.commands;

import com.carpet.rof.annotation.CarpetCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static com.carpet.rof.rules.autoFreeze.AutoFreezeSettings.commandUnfreeze;
import static net.minecraft.server.command.CommandManager.literal;

@CarpetCommand
public class UnfreezeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("unfreeze")
                .requires(source ->  carpet.utils.CommandHelper.canUseCommand(source, commandUnfreeze))
                .executes(context->{
                    context.getSource().getServer().getTickManager().setFrozen(false);
                    return 0;
                }));
    }
}
