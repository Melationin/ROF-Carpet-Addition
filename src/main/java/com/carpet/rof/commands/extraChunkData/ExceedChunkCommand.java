package com.carpet.rof.commands.extraChunkData;

import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;


import com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.RofTool;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.command.argument.DimensionArgumentType;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;
import static com.carpet.rof.utils.RofTool.text;
import static com.carpet.rof.utils.RofTool.textS;


public class ExceedChunkCommand
{


    public static Text[] display(ServerWorld world)
    {
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;

        MutableText text1 = text("\n"+ROFTextTool.getWorldName(world.getDimensionEntry().getIdAsString())
                +"&r&7("+world.getDimensionEntry().getIdAsString()+")"
        );
        MutableText text2 = text("&7-{&ftopY}: &r&n&e{" + data.topY + "}", style -> style.withHoverEvent(
                        ROFWarp.showText(text("设为极大值表示禁用，否则应为自然生成的最高运动阻挡方块+1"))),
                style -> style.withHoverEvent(ROFWarp.showText(text("点击设置")))
                        .withClickEvent(ROFWarp.suggestCommand("/exceedChunkMarker "+ world.getDimensionEntry().getIdAsString() +" setTopY "))

        );

        MutableText text3 = text("&7-{&fchunksSetSize}: &r&n&e{" + data.getSize() + "}",
                style -> style.withHoverEvent(ROFWarp.showText(text("exceedChunk的数量"))),
                style -> style.withHoverEvent(ROFWarp.showText(text("点击以重载")))
                        .withClickEvent(ROFWarp.suggestCommand("/exceedChunkMarker "+ world.getDimensionEntry().getIdAsString() +" loadFromWorld")));

        return new Text[]{text1, text2, text3};
    }


    public static ServerWorld getWorldFromContext(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        if (ROFCommandHelper.hasArgument(ctx, "dimension")) {
            return DimensionArgumentType.getDimensionArgument(ctx, "dimension");
        } else {
            return ctx.getSource().getWorld();
        }
    }


    public static int loadFromWorld(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendFeedback(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        ServerWorld world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        boolean forceInterrupted = false;
        try {
            forceInterrupted = BoolArgumentType.getBool(ctx, "forceInterrupted");
        }catch (Exception ignored){}
        if (data.workerThread != null && data.workerThread .isAlive() && !forceInterrupted) {
            ctx.getSource().sendFeedback(()->text("&c有未结束的任务！如果需要强行停止请让forceInterrupted = true"),false);
            return 1;
        }
        if(data.workerThread != null &&data.workerThread.isAlive()) {
            data.workerThread.interrupt();
        }
        AtomicDouble progress = new AtomicDouble(0.0);
        data.loadFromWorld(world, progress);
        if(ctx.getSource().getPlayer() instanceof ServerPlayerEntity player) {
            ROFEvents.ServerTickEndTasks.register((server -> {

                if(player.isDisconnected()) return true;
                player.sendMessage(RofTool.processDisplay("[ECM]正在从文件中加载区块",progress.get()), true);
                if(progress.get() >= 1) {
                    player.sendMessage(text("&9[ECM]&r加载完成！ExceedChunk数量为"+ data.getSize()), false);
                    return true;
                }
                return false;
            }));
        }


        return 0;
    }

    public static int setTopY(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendFeedback(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        final ServerWorld world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        data.topY = IntegerArgumentType.getInteger(ctx, "topY");
        ctx.getSource().sendFeedback(()->text("[ECM]维度" + world.getDimensionEntry().getIdAsString() +" topY已设置为: " + data.topY),false);
        return 0;
    }
    public static int clear(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendFeedback(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        final ServerWorld world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        data.clear();
        ctx.getSource().sendFeedback(()->text("[ECM]维度" + world.getDimensionEntry().getIdAsString() +"已清除"),false);
        return 0;
    }

    public static int save(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendFeedback(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        ServerWorld world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world);
        RofTool.saveNBT2Data(world,"extraWorldData",data.toNbt());
        return 1;
    }


    public static void registerCommand(CommandNode<ServerCommandSource> commandNode){
        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(commandNode);
        helper.registerCommand("exceedChunkMarker{r} [dimension]", ctx ->
                {

                    if(!exceedChunkMarker){
                        ctx.getSource().sendFeedback(textS("&c未开启exceedChunkMarker!"),false);
                        return 0;
                    }

                    ctx.getSource().sendFeedback(textS("----------ExceedChunkMarker----------"),false);

                    if(ROFCommandHelper.hasArgument(ctx,"dimension")){
                        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx,"dimension");
                        for (Text text : display(world)) {
                            ctx.getSource().sendFeedback(() -> text, false);
                        }
                    }else {
                        for (ServerWorld world : ctx.getSource().getServer().getWorlds()) {
                            for (Text text : display(world)) {
                                ctx.getSource().sendFeedback(() -> text, false);
                            }
                        }
                    }
                    return 1;
                },
                helper.predicate(
                        s->carpet.utils.CommandHelper.canUseCommand(s,ExtraChunkDatasCommand.commandExceedChunkMarker)),
                DimensionArgumentType.dimension()
        );

        helper.registerCommand("exceedChunkMarker [dimension] setTopY <topY>",
                ExceedChunkCommand::setTopY,
                ROFCommandHelper.reused(),
                IntegerArgumentType.integer()
        );
        helper.registerCommand("exceedChunkMarker [dimension] clear",
                ExceedChunkCommand::clear,
                ROFCommandHelper.reused()
        );
        helper.registerCommand("exceedChunkMarker [dimension] loadFromWorld",
                ExceedChunkCommand::loadFromWorld,
                ROFCommandHelper.reused()
        );
    }
}
