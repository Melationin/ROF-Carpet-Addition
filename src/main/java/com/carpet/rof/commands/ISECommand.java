package com.carpet.rof.commands;
//? <26.2 {
import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import carpet.utils.Messenger;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.utils.ROFTextTool.textS;
//?}
//? <26.2 {
@ROFRule
@ROFCommand
//?}
public class ISECommand
{
    //? <26.2 {
    @Rule(
            categories = {ROF,FEATURE,CREATIVE,COMMAND},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "实体ID命令",
            description = "查看并控制实体id的命令",
            extra = {
                    "/entityID - 显示接下来5个即将生成的实体ID",
                    "/entityID show <count> - 显示指定数量的即将生成的实体ID",
                    "/entityID show <beginID> <endID> - 显示指定范围内的实体ID"
            }
    )
    public static String commandEntityID = "false";
    @Rule(
            categories = {ROF,FEATURE,CREATIVE,COMMAND},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "实体ID设置命令",
            description = "设置实体id的命令",

            extra = {
                    "/entityID set <id> - 将当前实体ID设置为指定值"
            }
    )
    public static String commandEntityIDSet = "false";

    @Rule(
            categories = {ROF,FEATURE,CREATIVE},
            strict = false
    )
    @QuickTranslations(
            name = "实体ID溢出周期",
            description = "设置为0表示禁用"

    )
    public static int entityIDOverflowPeriod = 0;

    private static int ShowEntityID(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        int count = 5;

        if(CommandHelper.hasArgument(context, "count")){
            count = IntegerArgumentType.getInteger(context, "count");
        }
        if(count >= 2048){
            Messenger.m(context.getSource(), "rb " + "Too many entities to display!");
            return 0;
        }
        var CURRENT_ID = Entity.ENTITY_COUNTER;
        var world = context.getSource().getLevel();
        context.getSource().sendSuccess(
                textS(""),false
        );
        context.getSource().sendSuccess(
                textS("Current ID is " + CURRENT_ID.get() + ". Next entity ID will be " + (CURRENT_ID.get() + 1)),false
        );
        if(count>0){
            context.getSource().sendSuccess(
                    textS("---------- Next Entities ----------"),false
            );
            for(int i = 0;i<count;i++){
                int id = CURRENT_ID.get() + i + 1;
                if( world.getEntity(id)!= null){
                    Entity entity = world.getEntity(id);
                    context.getSource().sendSuccess(
                            textS("ID: "+id +  " Entity: "+entity.getName().getString()+" Distance: " + (i+1)),false
                    );
                }

            }
        }
        return 1;
    }
    private static int ShowEntityID2(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        int begin = IntegerArgumentType.getInteger(context, "begin");

        int end = IntegerArgumentType.getInteger(context, "end");

        if(end < begin || (end - begin) >= 2048){
            Messenger.m(context.getSource(), "rb " + "Invalid range");
            return 0;
        }

        var CURRENT_ID = Entity.ENTITY_COUNTER;
        var world = context.getSource().getLevel();
        context.getSource().sendSuccess(
                textS(""),false
        );
        context.getSource().sendSuccess(
                textS("Current ID is " + CURRENT_ID.get()),false
        );
        context.getSource().sendSuccess(
                textS("----------------------------------"),false
        );
        for(int i = begin;i<=end;i++){
            int id =i ;
            if( world.getEntity(id)!= null){
                Entity entity = world.getEntity(id);
                context.getSource().sendSuccess(
                        textS("ID: "+id +  " Entity: "+entity.getName().getString()),false
                );
            }
        }
        return 1;
    }
    private static int SetEntityID(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {


        int id = IntegerArgumentType.getInteger(context, "id");

        var CURRENT_ID = Entity.ENTITY_COUNTER;
        CURRENT_ID.set(id);
        context.getSource().sendSuccess(textS("Set current entity ID to: " + id),true);
        return 1;
    }


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        var helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("entityID{r}")
                .rCarpet(()->commandEntityID)
                .command(ISECommand::ShowEntityID);

        helper.registerCommand("entityID show [count]")
                .arg(IntegerArgumentType.integer(0))
                .command(ISECommand::ShowEntityID);
        helper.registerCommand("entityID show <begin> <end>")
                .arg(IntegerArgumentType.integer())
                .arg(IntegerArgumentType.integer())
                .command(ISECommand::ShowEntityID2);
        helper.registerCommand("entityID set{r} <id>")
                .rCarpet(()->commandEntityIDSet)
                .arg(IntegerArgumentType.integer())
                .command(ISECommand::SetEntityID);

    }
    //?}
}
