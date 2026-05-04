package com.carpet.rof.commands.extraChunkData;

import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.extraWorldData.ExtraWorldDatas;


import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.ROFTool;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.DimensionArgument;

import static com.carpet.rof.rules.extraChunkDatas.ExceedChunkMarkerSetting.exceedChunkMarker;
import static com.carpet.rof.utils.ROFTextTool.text;
import static com.carpet.rof.utils.ROFTextTool.textS;


public class ExceedChunkCommand
{


    public static Component[] display(ServerLevel world)
    {
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;

        MutableComponent text1 = text("\n"+ROFTextTool.getWorldName(world.dimensionTypeRegistration().getRegisteredName())
                +"&r&7("+world.dimensionTypeRegistration().getRegisteredName()+")"
        );
        MutableComponent text2 = text("&7-{&ftopY}: &r&n&e{" + data.topY + "}", style -> style.withHoverEvent(
                        ROFWarp.showText(text("设为极大值表示禁用，否则应为自然生成的最高运动阻挡方块+1"))),
                style -> style.withHoverEvent(ROFWarp.showText(text("点击设置")))
                        .withClickEvent(ROFWarp.suggestCommand("/exceedChunkMarker "+ world.dimensionTypeRegistration().getRegisteredName() +" setTopY "))

        );

        MutableComponent text3 = text("&7-{&fchunksSetSize}: &r&n&e{" + data.getSize() + "}",
                style -> style.withHoverEvent(ROFWarp.showText(text("exceedChunk的数量"))),
                style -> style.withHoverEvent(ROFWarp.showText(text("点击以重载")))
                        .withClickEvent(ROFWarp.suggestCommand("/exceedChunkMarker "+ world.dimensionTypeRegistration().getRegisteredName() +" loadFromWorld")));

        return new Component[]{text1, text2, text3};
    }


    public static ServerLevel getWorldFromContext(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (CommandHelper.hasArgument(ctx, "dimension")) {
            return DimensionArgument.getDimension(ctx, "dimension");
        } else {
            return ctx.getSource().getLevel();
        }
    }


    public static int loadFromWorld(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendSuccess(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        ServerLevel world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        boolean forceInterrupted = false;
        try {
            forceInterrupted = BoolArgumentType.getBool(ctx, "forceInterrupted");
        }catch (Exception ignored){}
        if (data.workerThread != null && data.workerThread .isAlive() && !forceInterrupted) {
            ctx.getSource().sendSuccess(()->text("&c有未结束的任务！如果需要强行停止请让forceInterrupted = true"),false);
            return 1;
        }
        if(data.workerThread != null &&data.workerThread.isAlive()) {
            data.workerThread.interrupt();
        }
        AtomicDouble progress = new AtomicDouble(0.0);
        data.loadFromWorld(world, progress);
        if(ctx.getSource().getPlayer() instanceof ServerPlayer player) {
            ROFEvents.ServerTickEndTasks.register((server -> {

                if(player.hasDisconnected()) return true;
                player.sendSystemMessage  (ROFTextTool.processDisplay("[ECM]正在从文件中加载区块",progress.get()), true);
                if(progress.get() >= 1) {
                    player.sendSystemMessage(text("&9[ECM]&r加载完成！ExceedChunk数量为"+ data.getSize()), false);
                    return true;
                }
                return false;
            }));
        }


        return 0;
    }

    public static int setTopY(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendSuccess(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        final ServerLevel world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        data.topY = IntegerArgumentType.getInteger(ctx, "topY");
        ctx.getSource().sendSuccess(()->text("[ECM]维度" + world.dimensionTypeRegistration().getRegisteredName() +" topY已设置为: " + data.topY),false);
        return 0;
    }
    public static int clear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendSuccess(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        final ServerLevel world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world).exceedChunkMarker;
        data.clear();
        ctx.getSource().sendSuccess(()->text("[ECM]维度" + world.dimensionTypeRegistration().getRegisteredName() +"已清除"),false);
        return 0;
    }

    public static int save(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        if(!exceedChunkMarker){
            ctx.getSource().sendSuccess(textS("&c未开启exceedChunkMarker!"),false);
            return 0;
        }
        ServerLevel world = getWorldFromContext(ctx);
        if(world == null) return 1;
        var data = ExtraWorldDatas.fromWorld(world);
        ROFTool.saveNBT2Data(world,"extraWorldData",data.toNbt());
        return 1;
    }


    public static void registerCommand(CommandNode<CommandSourceStack> commandNode){
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(commandNode);
        helper.registerCommand("exceedChunkMarker{r} [dimension]")
                .rCarpet(()->ExtraChunkDatasCommand.commandExceedChunkMarker)
                .arg(DimensionArgument.dimension())
                .command( ctx ->
                {

                    if(!exceedChunkMarker){
                        ctx.getSource().sendSuccess(textS("&c未开启exceedChunkMarker!"),false);
                        return 0;
                    }

                    ctx.getSource().sendSuccess(textS("----------ExceedChunkMarker----------"),false);

                    if(CommandHelper.hasArgument(ctx,"dimension")){
                        ServerLevel world = DimensionArgument.getDimension(ctx,"dimension");
                        for (Component text : display(world)) {
                            ctx.getSource().sendSuccess(() -> text, false);
                        }
                    }else {
                        for (ServerLevel world : ctx.getSource().getServer().getAllLevels()) {
                            for (Component text : display(world)) {
                                ctx.getSource().sendSuccess(() -> text, false);
                            }
                        }
                    }
                    return 1;
                });
        helper.registerCommand("exceedChunkMarker [dimension] setTopY <topY>")
                .reused()
                .arg(IntegerArgumentType.integer())
                .command(ExceedChunkCommand::setTopY);

        helper.registerCommand("exceedChunkMarker [dimension] clear")
                .reused()
                .command(ExceedChunkCommand::clear);
        helper.registerCommand("exceedChunkMarker [dimension] loadFromWorld")
                .reused()
                .command(ExceedChunkCommand::loadFromWorld);
    }
}
