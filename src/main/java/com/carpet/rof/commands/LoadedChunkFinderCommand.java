package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.utils.RofTool.text;
import static com.carpet.rof.utils.RofTool.textS;

@ROFCommand
@ROFRule
public class LoadedChunkFinderCommand
{
    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "记录加载区块命令",
                       description = "记录一段时间内活动的连通区块，用于查找被遗忘的区块加载器")
    public static String commandLoadedChunkFinder = "ops";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());

        Command<ServerCommandSource> command = (ctx)->{
            ServerWorld world = ROFCommandHelper.getArgumentOrDefault(ctx,"dimension",
                    ctx.getSource().getWorld(),
                    DimensionArgumentType::getDimensionArgument
                    );
            if(world == null){
                ctx.getSource().sendFeedback(()->text("&c未选择世界！"),false);
                return 1;
            }

            final var manager =  ExtraWorldDatas.fromWorld(world).loadedChunkManager;

            final int endTick =ROFCommandHelper.getArgumentOrDefault(ctx,"tick",
                    1,
                    IntegerArgumentType::getInteger
            );

            manager.loadedChunks.clear();
            manager.needLog = true;
            ctx.getSource().sendFeedback(textS("区块加载记录器已开启"),false);
            ROFEvents.ServerTickEndTasks.register((server,tick)-> {
                if(tick >= endTick){
                    manager.needLog = false;
                    int i =0;
                    var ret = manager.getConnectedChunks();
                    ctx.getSource().sendFeedback(textS("----------LoadedChunkFinder----------"),false);
                    ctx.getSource().sendFeedback(textS("已记录 "+tick +" tick\n"),false);
                    MutableText text1 = text(ROFTextTool.getWorldName(world.getDimensionEntry().getIdAsString())
                            +"&r&7("+world.getDimensionEntry().getIdAsString()+")"
                    );
                    ctx.getSource().sendFeedback(()->text1,false);
                    ctx.getSource().sendFeedback(textS("如下联通区块被加载: "),false);
                    for(var data : ret){
                        int j = i;
                        ctx.getSource().sendFeedback(()->text("&6#"+j+"&7->&a{" + data.getCenterChunk().toString() + "} &rsize: "+data.size(),
                        style -> style
                                .withHoverEvent(ROFWarp.showText(text("中心区块坐标(点击复制传送坐标)")))
                                .withClickEvent(ROFWarp.copyToClipboard(
                                        ROFTextTool.getStringToClip(data.getCenterChunk().getCenterAtY(
                                                (int) ctx.getSource().getPosition().getY()))
                                        ))
                        ),false);
                       i++;
                    }
                    ctx.getSource().sendFeedback(()->text("一共"+ret.size()+"个联通区域，共"+manager.loadedChunks.size()+"个区块"),false);
                    return true;
                }
                return false;
            });
            return 0;
        };

        helper.registerCommand("loadedChunkFinder{r}",command,
                ROFCommandHelper.carpetRequire(commandLoadedChunkFinder)
        );

        helper.registerCommand("loadedChunkFinder <dimension> [tick]",command,
                DimensionArgumentType.dimension(),
                IntegerArgumentType.integer(1)
                );
    }
}
