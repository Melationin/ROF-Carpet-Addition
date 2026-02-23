package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFCommand
public class UnfreezeCommand
{
    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    @QuickTranslations(
            name = "解冻命令",
            description = "如题。单独添加解冻命令，用与于高卡顿自动暂停配合使用",
            extra = {
                    "/unfreeze - 解冻服务器"
            }
    )
    public static String commandUnfreeze = "false";
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        new ROFCommandHelper<>(dispatcher.getRoot())
                .registerCommand("unfreeze{r}")
                .rCarpet(()->commandUnfreeze)
                .command(context -> {
                    context.getSource().getServer().getTickManager().setFrozen(false);
                    return 0;
                });
    }
}
