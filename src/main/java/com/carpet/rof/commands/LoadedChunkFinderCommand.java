package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.MutableComponent;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.utils.ROFTextTool.text;
import static com.carpet.rof.utils.ROFTextTool.textS;

@ROFCommand
@ROFRule
public class LoadedChunkFinderCommand
{
    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "记录加载区块命令",
                       description = "记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器",
                       extra = {"/loadedChunkFinder - 记录当前维度1 tick内的区块加载情况",
                             "/loadedChunkFinder <dimension> [tick] - 记录指定指定一段时间内的区块加载情况，tick默认为1"}
                       )
    public static String commandLoadedChunkFinder = "ops";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());

        Command<CommandSourceStack> command = (ctx)->{
            ServerLevel world = CommandHelper.getArgumentOrDefault(ctx,"dimension",
                    ctx.getSource().getLevel(),
                    DimensionArgument::getDimension
                    );
            if(world == null){
                ctx.getSource().sendSuccess(()->text("&c未选择世界！"),false);
                return 1;
            }

            final var manager =  ExtraWorldDatas.fromWorld(world).chunkLoadedFinder;

            final int endTick = CommandHelper.getArgumentOrDefault(ctx,"tick",
                    1,
                    IntegerArgumentType::getInteger
            );

            manager.ChunkLoadedMap.clear();
            manager.needLog = true;
            ctx.getSource().sendSuccess(textS("区块加载记录器已开启"),false);
            ROFEvents.ServerTickEndTasks.register((server,tick)-> {
                if(tick >= endTick){
                    manager.needLog = false;
                    int i =0;
                    var ret = manager.getConnectedChunks();
                    ctx.getSource().sendSuccess(textS("----------LoadedChunkFinder----------"),false);
                    ctx.getSource().sendSuccess(textS("已记录 "+tick +" tick\n"),false);
                    MutableComponent text1 = text(ROFTextTool.getWorldName(world.dimensionTypeRegistration().getRegisteredName())
                            +"&r&7("+world.dimensionTypeRegistration().getRegisteredName()+")"
                    );
                    ctx.getSource().sendSuccess(()->text1,false);
                    ctx.getSource().sendSuccess(textS("如下联通区块被加载: "),false);
                    for(var data : ret){
                        int j = i;
                        ctx.getSource().sendSuccess(()->text("&6#"+j+"&7->&a{" + data.getCenterChunk().toString() + "} &rsize: "+data.size(),
                        style -> style
                                .withHoverEvent(ROFWarp.showText(text("中心区块坐标(点击复制传送坐标)")))
                                .withClickEvent(ROFWarp.copyToClipboard(
                                        ROFTextTool.getStringToClip(data.getCenterChunk().getMiddleBlockPosition(
                                                (int) ctx.getSource().getPosition().y()))
                                        ))
                        ),false);
                       i++;
                    }
                    ctx.getSource().sendSuccess(()->text("一共"+ret.size()+"个联通区域，共"+manager.ChunkLoadedMap.size()+"个区块"),false);
                    return true;
                }
                return false;
            });
            return 0;
        };

        helper.registerCommand("loadedChunkFinder{r}")
                .rCarpet(()->commandLoadedChunkFinder)
                .command(command);
        helper.registerCommand("loadedChunkFinder <dimension> [tick]")
                .arg(DimensionArgument.dimension())
                .arg(IntegerArgumentType.integer(1))
                .command(command);
    }
}
