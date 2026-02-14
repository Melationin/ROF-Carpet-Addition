package com.carpet.rof.commands.extraChunkData;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFCommand
@ROFRule
public class ExtraChunkDatasCommand
{
    @Rule(
            categories = {COMMAND,ROF},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "超高度区块标记器(ECM)命令",
            description = "控制ECM命令权限等级。在未开始ECM时无效"
    )
    public static String commandExceedChunkMarker= "ops";

    @Rule(
            categories = {COMMAND,ROF},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "区块修改记录器命令",
            description = "区块修改记录器等级。在未开始区块修改记录器时无效"
    )
    public static String commandChunkModifyCommand= "ops";





    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {


        ExceedChunkCommand.registerCommand(dispatcher.getRoot());
        ChunkModifyCommand.registerCommand(dispatcher.getRoot());

    }
}
