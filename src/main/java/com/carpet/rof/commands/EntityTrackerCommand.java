package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.RofTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;


@ROFCommand
@ROFRule
public class EntityTrackerCommand
{

    @Rule(
            categories = {COMMAND,ROF},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "实体追踪",
            description = "可以选择一个实体进行追踪，其坐标与位置将会实时广播给玩家。"
    )
    public static String commandEntityTracker = "true";

    public static Map<UUID, UUID> entityTrackerMap = new HashMap<>();

    static {
        ROFEvents.ServerStart.register(
                minecraftServer -> {
                    entityTrackerMap.clear();
                }
        );
        ROFEvents.ServerTickEnd.register(
                minecraftServer -> {
                    for(Map.Entry<UUID, UUID> entry : entityTrackerMap.entrySet()){
                        if(entry.getValue() == null){continue;}
                        var player = minecraftServer.getPlayerManager().getPlayer(entry.getKey());
                        if(player != null){
                            Entity entity = getEntity(minecraftServer, entry.getValue());
                            if (entity != null) {
                                var text = RofTool.text("Pos: &9"+ RofTool.toString_(RofTool.getPos_(entity))+" &rVec: &9"+RofTool.toString_(entity.getVelocity()));
                                player.sendMessage(text,true);
                                if(entity.isRemoved()){
                                    player.sendMessage(Text.literal("因为实体移除，实体追踪结束"),false);
                                    entityTrackerMap.replace(entry.getKey(),null);
                                }
                            }
                        }
                    }
                }
        );
    }
    @Nullable
    public static Entity getEntity(MinecraftServer server, UUID uuid)
    {
        Entity entity = null;
        for(var world:server.getWorlds()){
            Entity entity1 = world.getEntity(uuid);
            if(entity1 != null){
                entity = entity1;
            }
        }
        return entity;
    }


    public static int addNormalEntity(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
    {


        Entity entity =  EntityArgumentType.getEntity(ctx,"entity");

        if(ctx.getSource().getPlayer()!=null){
            ctx.getSource().getPlayer().sendMessage(Text.literal("实体追踪开始，当前实体: ").append(entity.getName()));
            entityTrackerMap.put(ctx.getSource().getPlayer().getUuid(),entity.getUuid());
            return 0;
        }
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
       dispatcher.register(
               literal("entityTracker")
                       .requires(source-> carpet.utils.CommandHelper.canUseCommand(source, commandEntityTracker))
                       .then(literal("set").then(
                               argument("entity", EntityArgumentType.entity()).executes(EntityTrackerCommand::addNormalEntity)
                       ))
                       .then(literal("clear").executes(ctx->{
                           if(ctx.getSource().getPlayer()!=null){
                               entityTrackerMap.put(ctx.getSource().getPlayer().getUuid(),null);
                               return 0;
                           }
                           return 1;
                       }))

       );
    }
}
