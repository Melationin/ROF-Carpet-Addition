package com.carpet.rof.commands;

import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static com.carpet.rof.rules.autoFreeze.AutoFreezeSettings.commandUnfreeze;

@ROFCommand
public class UnfreezeCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        new ROFCommandHelper<ServerCommandSource>(dispatcher.getRoot())
                .registerCommand("unfreeze{r}")
                .rCarpet(()->commandUnfreeze)
                .command(context -> {
                    context.getSource().getServer().getTickManager().setFrozen(false);
                    return 0;
                });
    }
}
