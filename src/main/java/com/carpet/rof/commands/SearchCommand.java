package com.carpet.rof.commands;


import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import com.carpet.rof.annotation.CarpetCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static carpet.utils.Translations.tr;
import static com.carpet.rof.rules.commands.CommandSetting.commandRulesSearcher;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@CarpetCommand
public class SearchCommand {

    static String  identifier = "carpet";


    public static class CategoriesSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

            for (String str:CarpetServer.settingsManager.getCategories()) {
                builder.suggest(str);
            }

            return builder.buildFuture();
        }
    }




    private static Text makeSetRuleButton(CarpetRule<?> rule, String option, boolean brackets)
    {
        String style = RuleHelper.isInDefaultValue(rule)?"g":(option.equalsIgnoreCase(RuleHelper.toRuleString(rule.defaultValue()))?"e":"y");
        if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.value())))
        {
            style = style + "u";
            if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.defaultValue())))
                style = style + "b";
        }
        String component = style + (brackets ? " [" : " ") + option + (brackets ? "]" : "");
        if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.value())))
            return Messenger.c(component);

        return Messenger.c(component, "^g "+ tr(TranslationKeys.SWITCH_TO).formatted(option + (option.equals(RuleHelper.toRuleString(rule.defaultValue()))?" (default)":"")), "?/"+identifier+" " + rule.name() + " " + option);
    }

    private static Text displayInteractiveSetting(CarpetRule<?> rule)
    {
        String displayName = RuleHelper.translatedName(rule);
        List<Object> args = new ArrayList<>();
        args.add("w - "+ displayName +" ");
        args.add("!/"+identifier+" "+rule.name());
        args.add("^y "+RuleHelper.translatedDescription(rule));
        for (String option: rule.suggestions())
        {
            args.add(makeSetRuleButton(rule, option, true));
            args.add("w  ");
        }
        if (!rule.suggestions().contains(RuleHelper.toRuleString(rule.value())))
        {
            args.add(makeSetRuleButton(rule, RuleHelper.toRuleString(rule.value()), true));
            args.add("w  ");
        }
        args.remove(args.size()-1);
        return Messenger.c(args.toArray(new Object[0]));
    }


    public static void executeCommand(ServerCommandSource source,String key,boolean enable,String category)
    {
        SettingsManager manager = CarpetServer.settingsManager;
        Messenger.m(source, " ");
        Messenger.m(source, "wb 搜索结果为: ");
        int i = 0;
        for(var c : manager.getCarpetRules()){
            String allText = "";
            allText +=  c.name();
            allText += RuleHelper.translatedDescription(c);
            if(allText.toLowerCase().contains(key.toLowerCase())
            &&(category==null || c.categories().contains(category))
                    &&(!enable || !RuleHelper.isInDefaultValue(c))
            ) {
                Messenger.m(source, displayInteractiveSetting(c));
                i++;
            }
        }
        Messenger.m(source, "wb 一共搜索到"+i+"条规则");
    }

    public static void registerDispatcher(CommandDispatcher<ServerCommandSource> dispatcher){

        CommandNode<ServerCommandSource> root = dispatcher.getRoot();
        CommandNode<ServerCommandSource> carpetNode = root.getChild("carpet");


        LiteralArgumentBuilder<ServerCommandSource> mineCmd =


                literal("search").requires((source )->{
                   return carpet.utils.CommandHelper.canUseCommand(source, commandRulesSearcher);
                }).then(argument("key", StringArgumentType.string()).executes((context -> {
                    String key = StringArgumentType.getString(context, "key");
                    executeCommand(context.getSource(),key,false,null);
                    return 0;
                        }))
                .then(argument("isNoDefaultValue", BoolArgumentType.bool()).executes(
                        context -> {
                            String key = StringArgumentType.getString(context, "key");
                            boolean brackets = BoolArgumentType.getBool(context, "isNoDefaultValue");
                            executeCommand(context.getSource(),key,brackets,null);
                            return 0;
                        }



        ).then(argument("category",StringArgumentType.string()).suggests(
        new CategoriesSuggestionProvider()
        ).executes(context -> {
            String key = StringArgumentType.getString(context, "key");
            boolean brackets = BoolArgumentType.getBool(context, "isNoDefaultValue");
            String category = StringArgumentType.getString(context, "category");
            executeCommand(context.getSource(),key,brackets,category);
            return 0;
        }))));
        carpetNode.addChild(mineCmd.build());
    }
}
