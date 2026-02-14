package com.carpet.rof.commands.extraChunkData;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static com.carpet.rof.rules.extraChunkDatas.ChunkModifySetting.chunkModifyLogger;
import static com.carpet.rof.utils.RofTool.text;
import static com.carpet.rof.utils.RofTool.textS;

public class ChunkModifyCommand
{


    public static Text[] display(ServerWorld world)
    {


        var data = ExtraWorldDatas.fromWorld(world).chunkModifyData;

        MutableText text1 = text(ROFTextTool.getWorldName(world.getDimensionEntry().getIdAsString())
        +"&r&7("+world.getDimensionEntry().getIdAsString()+")"
        );

        MutableText text2 = text("&7-{&fminChunkLifetime}: &r&n&e{" + data.minChunkLifetime + "}", style -> style.withHoverEvent(
                        ROFWarp.showText(text("只有最后修改时间与创建时间之差小于这个值的才会保存。"))),
                style -> style.withHoverEvent(ROFWarp.showText(text("点击设置")))
                        .withClickEvent(ROFWarp.suggestCommand("/chunkModifyLogger "+ world.getDimensionEntry().getIdAsString() +" setMinChunkLifetime "))

        );

        MutableText text3 = text("&7-{&fchunksSetSize}: &r&e{" + data.getSize()+ "}",
                style -> style.withHoverEvent(ROFWarp.showText(text("将会被保存的区块数量")))
        );
        MutableText text4 = text("");
        return new Text[]{ text4 ,text1, text2, text3};
    }


    public static void registerCommand(CommandNode<ServerCommandSource> commandNode){

        final Command<ServerCommandSource> show = ctx -> {
            if(!chunkModifyLogger){
                ctx.getSource().sendFeedback(textS("&c未开启chunkModifyLogger!"),false);
                return 0;
            }
            ctx.getSource().sendFeedback(textS("----------ChunkModifyLogger----------"),false);

            if(ROFCommandHelper.hasArgument(ctx,"dimension")){
                ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx,"dimension");
                for(var text: display(world)){
                    ctx.getSource().sendFeedback(()->text,false);
                    return 0;
                }
            }else {
                for(var world: ctx.getSource().getServer().getWorlds()){
                    for(var text: display(world)){
                        ctx.getSource().sendFeedback(()->text,false);
                    }
                }
                return 0;
            }
            return 1;
        };

        final Command<ServerCommandSource> setMinChunkLifetime = context -> {
            if(!chunkModifyLogger){
                context.getSource().sendFeedback(textS("&c未开启chunkModifyLogger!"),false);
                return 0;
            }

             ServerWorld world =  ROFCommandHelper.getArgumentOrDefault(context,"dimension",
                     context.getSource().getWorld(),
                     DimensionArgumentType::getDimensionArgument
             );
             if(world==null){
                 context.getSource().sendFeedback(()->text("&c未选择世界！"),false);
                 return 1;
             }else {
                 var data =  ExtraWorldDatas.fromWorld(world).chunkModifyData;
                 data.minChunkLifetime = IntegerArgumentType.getInteger(context,"minChunkLifetime");
                 return 0;
             }
        };

        final Command<ServerCommandSource> clear = context -> {
            if(!chunkModifyLogger){
                context.getSource().sendFeedback(textS("&c未开启chunkModifyLogger!"),false);
                return 0;
            }

            ServerWorld world =  ROFCommandHelper.getArgumentOrDefault(context,"dimension",
                    context.getSource().getWorld(),
                    DimensionArgumentType::getDimensionArgument
            );
            if(world==null){
                context.getSource().sendFeedback(()->text("&c未选择世界！"),false);
                return 1;
            }else {
                var data =  ExtraWorldDatas.fromWorld(world).chunkModifyData;
                data.clear();
                return 0;
            }
        };

        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(commandNode);
        helper.registerCommand("chunkModifyLogger{r} [dimension]",show,
                helper.predicate((source)->carpet.utils.CommandHelper.canUseCommand(source,ExtraChunkDatasCommand.commandChunkModifyCommand)),
                DimensionArgumentType.dimension());
        helper.registerCommand("chunkModifyLogger [dimension] setMinChunkLifetime <minChunkLifetime>",setMinChunkLifetime,
                ROFCommandHelper.reused(),
                IntegerArgumentType.integer());
        helper.registerCommand("chunkModifyLogger [dimension] clear",clear,
                ROFCommandHelper.reused());
    }
}
