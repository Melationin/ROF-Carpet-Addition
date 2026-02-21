package com.carpet.rof.commands.chunkFilter;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.rules.extraChunkDatas.ChunkModifySetting;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

import java.io.IOException;
import java.util.function.Function;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.utils.ROFTextTool.text;


@ROFRule
@ROFCommand
public class ChunkFilterCommand
{

    public static final SuggestionProvider<ServerCommandSource> CHUNK_POS_SUGGEST = (context, builder) ->
    {
        ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(context.getSource().getPosition()));
        builder.suggest(chunkPos.x + " " + chunkPos.z);
        return builder.buildFuture();
    };
    @Rule(

            categories = {ROF, COMMAND}, strict = false, validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "区块过滤器", description = "可以通过这个命令筛选区块，并以MCA支持的格式输出。用于删区块",
            extra = {
                    "/chunkFilter load [dimension] : 加载某维度区块数据。一次只能加载一个维度的数据。默认无区块被筛选,即空区块",
                    "/chunkFilter save [string>: 输出筛完的区块，输出文件将以cvs格式输出，并存储在维度的地图存档中",
                    "/chunkFilter add/remove [chunkPos] : 添加或删除单区块",
                    "/chunkFilter add/remove rect [from] [to] : 添加或删除矩形区域",
                    "/chunkFilter add/remove inhabitedTime [value]: 玩家停留时间大于等于value的区块将会被添加或删除",
                    "/chunkFilter add/remove modifyTime [value]: 修改时间小于等于value的区块将会被添加或删除. 在区块更新记录器未开启时无效，且此值应该低于等于维度minChunkLifetime",
                    "/chunkFilter extend [distance]: 将过滤器里的区块向外扩展distance个区块( 以切比雪夫距离)",
                    "/chunkFilter reverse: 反转现有区块", "/chunkFilter clear: 清空过滤器里的区块",
                    "/chunkFilter close: 关闭过滤器"
            })
    public static String commandChunkFilter = "ops";
    static ChunkFilterThread thread = null;

    static Function<ServerCommandSource, Boolean> canExecute = (ServerCommandSource s) ->
    {

        if (thread == null) {
            s.sendFeedback(() -> Text.of("[ChunkFilter]未加载"), false);
            return false;
        }
        if (thread.chunkFilter.progress != 1) {
            s.sendFeedback(() -> Text.of("[ChunkFilter]任务进行中..."), false);
            return false;
        }
        return true;

    };

    public static int changeRect(CommandContext<ServerCommandSource> ctx,boolean add){
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        ColumnPos from,to;
        if(ROFCommandHelper.hasArgument(ctx,"from")){
            from = ColumnPosArgumentType.getColumnPos(ctx, "from");
            to = ColumnPosArgumentType.getColumnPos(ctx, "to");


        }else if(ROFCommandHelper.hasArgument(ctx,"chunkPos")){
            from = to = ColumnPosArgumentType.getColumnPos(ctx, "chunkPos");
        }else return 1;
        thread.asyncFunc(() ->
        {
            if(add) thread.chunkFilter.addRect(from.x(), from.z(), to.x(), to.z());
            else thread.chunkFilter.removeRect(from.x(), from.z(), to.x(), to.z());
        }, "changeRect" ,ctx.getSource());
        return 0;
    }



    public static int changeModifyTime(CommandContext<ServerCommandSource> ctx,boolean add){
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        if(!ChunkModifySetting.chunkModifyLogger){
            ctx.getSource().sendFeedback(()->text("&c未开启区块修改记录器！"),false);
            return 1;
        }
        int value = IntegerArgumentType.getInteger(ctx, "value");
        thread.asyncFunc(() ->
        {
            if(add) thread.chunkFilter.addByModifyTime(value);
            else thread.chunkFilter.removeByModifyTime(value);
        }, "changeModifyTimeChunk",ctx.getSource());
        return 0;
    }
    public static int changeInhabitedTime(CommandContext<ServerCommandSource> ctx,boolean add){
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        int value = IntegerArgumentType.getInteger(ctx, "value");
        thread.asyncFunc(() ->
        {
            if(add) thread.chunkFilter.addByInhabitedTime(value);
            else thread.chunkFilter.removeByInhabitedTime(value);
        }, "changeInhabitedTimeChunk",ctx.getSource());
        return 0;
    }
    public static int extend(CommandContext<ServerCommandSource> ctx)
    {
        int distance = IntegerArgumentType.getInteger(ctx, "distance");
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        thread.asyncFunc(() ->
        {
            thread.chunkFilter.extend(distance);
        },"extend", ctx.getSource());
        return 0;
    }

    public static int reverse(CommandContext<ServerCommandSource> ctx)
    {
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        thread.asyncFunc(() ->
        {
            thread.chunkFilter.reverse();
        }, "reverse",ctx.getSource());
        return 0;
    }

    public static int clear(CommandContext<ServerCommandSource> ctx)
    {
        if (!canExecute.apply(ctx.getSource())) {
            return 1;
        }
        thread.asyncFunc(() ->
        {
            thread.chunkFilter.clearChunks();
        },"clear", ctx.getSource());
        return 0;
    }

    public static int close(CommandContext<ServerCommandSource> ctx){
        if (thread == null) {
            ctx.getSource().sendFeedback(() -> Text.of("[ChunkFilter]未加载"), false);
            return 1;
        }
        thread.close();
        thread = null;
        ctx.getSource().sendFeedback(() -> Text.of("[ChunkFilter]已关闭"), false);
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {

        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());

        helper.registerCommand("chunkFilter{r}",context ->
        {
            if (thread == null) {
                context.getSource().sendFeedback(() -> Text.of("[ChunkFilter]未加载"), false);
                return 1;
            } else {
                context.getSource()
                        .sendFeedback(() -> Text.of("[ChunkFilter]已加载区块： " + thread.chunkFilter.getLoadedChunks()),
                                false);
                context.getSource().sendFeedback(
                        () -> Text.of("[ChunkFilter]已筛选区块： " + thread.chunkFilter.getFilteredChunks()), false);
                return 0;
            }
        },
                helper.predicate(source -> carpet.utils.CommandHelper.canUseCommand(source, commandChunkFilter)));

        ROFCommandHelper<ServerCommandSource> helper2 = new ROFCommandHelper<>(dispatcher.getRoot().getChild("chunkFilter"));

        helper2.registerCommand("load <dimension>",ctx ->
        {
            ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
            thread = new ChunkFilterThread(world);
            thread.asyncFunc(() ->
            {
                try {
                    thread.chunkFilter.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "loadFromWorld",ctx.getSource());
            return 0;
        },DimensionArgumentType.dimension());

        helper2.registerCommand("save <filename>",ctx ->
        {
            String filename = StringArgumentType.getString(ctx, "filename");
            if (!canExecute.apply(ctx.getSource())) {
                return 1;
            }
            thread.asyncFunc(() ->
            {
                try {
                    thread.chunkFilter.save(filename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },"loadFromWorld", ctx.getSource());
            return 0;
        }, StringArgumentType.string());
        helper2.registerCommand("add rect <from>{s} <to>{s}",ctx ->changeRect(ctx,true),
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST,
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST);
        helper2.registerCommand("remove rect <from>{s} <to>{s}",ctx ->changeRect(ctx,false),
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST,
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST);
        helper2.registerCommand("add <chunkPos>{s}",ctx ->changeRect(ctx,true),
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST);
        helper2.registerCommand("remove <chunkPos>{s}",ctx ->changeRect(ctx,false),
                ColumnPosArgumentType.columnPos(),
                CHUNK_POS_SUGGEST);
        helper2.registerCommand("add inhabitedTime <value>",ctx->changeInhabitedTime(ctx,true),
                IntegerArgumentType.integer());
        helper2.registerCommand("remove inhabitedTime <value>",ctx->changeInhabitedTime(ctx,false),
                IntegerArgumentType.integer());
        helper2.registerCommand("add modifyTime <value>",ctx->changeModifyTime(ctx,true),
                IntegerArgumentType.integer());
        helper2.registerCommand("remove modifyTime <value>",ctx->changeModifyTime(ctx,false),
                IntegerArgumentType.integer());
        helper2.registerCommand("extend <distance>",ChunkFilterCommand::extend, IntegerArgumentType.integer());
        helper2.registerCommand("reverse",ChunkFilterCommand::reverse);
        helper2.registerCommand("clear",ChunkFilterCommand::clear);
        helper2.registerCommand("close",ChunkFilterCommand::close);
    }

}
