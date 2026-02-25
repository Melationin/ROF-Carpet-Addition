package com.carpet.rof.commands;

import carpet.CarpetServer;
import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.utils.ROFTextTool.textS;
import static com.mojang.text2speech.Narrator.LOGGER;

@ROFCommand
@ROFRule
public class RequirementModifyCommand
{

    /** type: 0 set, 1 add, -1 or */
    public record RequirementModify(String permission, int type) { }


    public static Map<String,RequirementModify> requirementModifyMap = new HashMap<String,RequirementModify>();

    public static void initialization(MinecraftServer server, ROFConfig config){
        Type type = new TypeToken<Map<String, RequirementModify>>(){}.getType();
        Gson gson = new Gson();
        requirementModifyMap = gson.fromJson(ROFConfig.INSTANCE.get("requirementModifyMap"), type);
        if(requirementModifyMap == null){
            requirementModifyMap = new HashMap<String,RequirementModify>();
        }
        ROFEvents.ServerTickEndTasks.register(serverTick -> {
            ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(server.getCommandManager().getDispatcher().getRoot());
            for (var entry : requirementModifyMap.entrySet()) {
                try {
                    helper.setCommandRequirement(entry.getKey(), ((source, aBoolean) ->
                    {
                        if (!requirementModifyMap.containsKey(entry.getKey())) {
                            return aBoolean;
                        } else {
                            var requirementModify = requirementModifyMap.get(entry.getKey());
                            switch (requirementModify.type()) {
                                case 0 -> {
                                    return carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission());
                                }
                                case -1 -> {
                                    return aBoolean || carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission());
                                }
                                case 1 -> {
                                    return aBoolean && carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission());
                                }
                            }
                            return aBoolean;
                        }
                    }));
                }catch (ROFCommandHelper.SetCommandPredicateError error){
                    LOGGER.error(error.getMessage());
                }
            }
            return true;
        });
    }


    @Rule(
            categories = {COMMAND,CREATIVE},
            validators = Validators.CommandLevel.class,
            strict = false
    )
    @QuickTranslations(
            name = "命令权限修改命令",
            description = "修改指定命令的权限要求",
            extra = {
                    "/requirementModify <commandPath> <permission> set - 将指定命令的权限要求设置为指定权限",
                    "/requirementModify <commandPath> <permission> add - 在原有权限要求的基础上添加一个权限要求",
                    "/requirementModify <commandPath> <permission> or - 在原有权限要求的基础上添加一个或条件的权限要求",
                    "/requirementModify <commandPath> clear - 清除指定命令的权限修改",
                    "/requirementModify clearAll - 清除所有命令的权限修改",
                    "/requirementModify list - 列出所有被修改权限要求的命令",
                    "对于commandPath ,用<>表示参数：",
                    "  - 例：/requirementModify \"player <player> shadow\" ops set",
                    "    表示将\"player <player> shadow\"的权限要求设置为ops"
            }
    )
    public static String commandRequirementModify = "ops";





    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {


        SuggestionProvider<ServerCommandSource> modSuggestion = (context, builder) -> {
            builder.suggest("add");
            builder.suggest("set");
            builder.suggest("or");
            return builder.buildFuture();
        };

        SuggestionProvider<ServerCommandSource> permissionSuggestion = (context, builder) -> {
            builder.suggest("0");
            builder.suggest("1");
            builder.suggest("2");
            builder.suggest("3");
            builder.suggest("4");
            builder.suggest("ops");
            builder.suggest("true");
            builder.suggest("false");
            return builder.buildFuture();
        };

        ROFCommandHelper<ServerCommandSource> helper= new ROFCommandHelper<>(dispatcher.getRoot());
        BiFunction<CommandContext<ServerCommandSource>,Integer,Integer> setCommand = ( context, type) -> {
            String commandPath = StringArgumentType.getString(context,"commandPath").trim();
            String permission = StringArgumentType.getString(context,"permission");
            try {

                boolean needSet = !requirementModifyMap.containsKey(commandPath);

                switch (type){
                    case 0->requirementModifyMap.put(commandPath,new RequirementModify(permission,0));
                    case  1->requirementModifyMap.put(commandPath,new RequirementModify(permission,1));
                    case -1->requirementModifyMap.put(commandPath,new RequirementModify(permission,-1));
                }
                if(needSet) {
                    helper.setCommandRequirement(commandPath, ((source, aBoolean) ->
                    {
                        if (!requirementModifyMap.containsKey(commandPath)) {
                            return aBoolean;
                        } else {
                            var requirementModify = requirementModifyMap.get(commandPath);
                            switch (requirementModify.type) {
                                case 0 -> {
                                    return carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission);
                                }
                                case -1 -> {
                                    return aBoolean || carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission);
                                }
                                case 1 -> {
                                    return aBoolean && carpet.utils.CommandHelper.canUseCommand(source,
                                            requirementModify.permission);
                                }
                            }
                            return aBoolean;
                        }

                    }));

                }
                context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player ->
                {
                    context.getSource().getServer().getCommandManager().sendCommandTree(player);
                });
                context.getSource().sendFeedback(textS("&a修改命令权限成功！"), false);
                return 1;
            }
            catch (ROFCommandHelper.SetCommandPredicateError error) {
                requirementModifyMap.remove(commandPath);
                context.getSource().sendError(Text.of(error.getMessage()));
                return 0;
            }
        };



        helper.registerCommand("requirementModify{r} <commandPath> <permission>{s} set")
                .rCarpet(()->commandRequirementModify)
                .arg(StringArgumentType.string())
                .arg(StringArgumentType.word())
                .s(permissionSuggestion)
                .command(ctx->setCommand.apply(ctx,0));

        helper.registerCommand("requirementModify <commandPath> <permission> add")
                .reused()
                .reused()
                .command(ctx->setCommand.apply(ctx,1));

        helper.registerCommand("requirementModify <commandPath> <permission> or")
                .reused()
                .reused()
                .command(ctx->setCommand.apply(ctx,-1));

        helper.registerCommand("requirementModify <commandPath> clear")
                .reused()
                .command(ctx->{
                    String commandPath = StringArgumentType.getString(ctx,"commandPath").trim();

                    if(requirementModifyMap.containsKey(commandPath) && requirementModifyMap.get(commandPath).type!=2){
                        requirementModifyMap.put(commandPath,new RequirementModify("false",2));
                    }else  {
                        ctx.getSource().sendFeedback(textS("&e当前命令没有被修改过权限要求"),false);
                        return 0;
                    }
                    return 1;
                });

        helper.registerCommand("requirementModify clearAll")
                .command(ctx->{
                    requirementModifyMap.replaceAll((key, value) -> new RequirementModify("false",2));
                    return 1;
                });

        helper.registerCommand("requirementModify list")
                .command(context -> {

                    context.getSource().sendFeedback(textS("&6---------RequirementModify----------"), false);

                    for(Map.Entry<String, RequirementModify> entry: requirementModifyMap.entrySet()){
                        if(entry.getValue().type != 2) {
                            context.getSource().sendFeedback(
                                    textS("&6命令: &e/" + entry.getKey() + " &6权限要求: &e" + entry.getValue().permission + " &6修改类型: &e" + switch (entry.getValue().type) {
                                        case 0 -> "set";
                                        case 1 -> "add";
                                        case -1 -> "or";
                                        default -> "unknown";
                                    }), false);
                        }
                    }
                    return 1;
                });
    }
}
