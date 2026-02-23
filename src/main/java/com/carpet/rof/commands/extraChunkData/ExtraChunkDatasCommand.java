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
            description = "控制ECM命令权限等级。在未开始ECM时无效",
            extra = {"/exceedChunkMarker - 显示ECM当前的信息",
            "/exceedChunkMarker <dimension> - 显示选定维度的信息",
            "/exceedChunkMarker [dimension] setTopY <topY> - 设置选定维度的ECM高度（维度默认为玩家所在维度）",
            "/exceedChunkMarker [dimension] clear - 清除选定维度的ECM数据（维度默认为玩家所在维度）",
            "/exceedChunkMarker [dimension] loadFromWorld - 从选定维度的世界数据中加载ECM数据（维度默认为玩家所在维度）"}
    )
    public static String commandExceedChunkMarker= "ops";

    @Rule(
            categories = {COMMAND,ROF},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "区块修改记录器命令",
            description = "区块修改记录器等级。在未开始区块修改记录器时无效",
            extra = {"/chunkModify - 显示区块修改记录器的信息",
                    "/chunkModify <dimension> - 显示选定维度的信息",
                    "/chunkModify [dimension] clear - 清除选定维度的区块修改记录器数据（维度默认为玩家所在维度）",
                    "/chunkModifyLogger [dimension] setMinChunkLifetime <minChunkLifetime> - 设置选定维度的区块修改记录器最小区块生命周期（维度默认为玩家所在维度）",
                    " minChunkLifetime : 被记录的区块至少在创建后 minChunkLifetime tick内被修改过才会被记录"
            }
    )
    public static String commandChunkModifyCommand= "ops";





    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {


        ExceedChunkCommand.registerCommand(dispatcher.getRoot());
        ChunkModifyCommand.registerCommand(dispatcher.getRoot());

    }
}
