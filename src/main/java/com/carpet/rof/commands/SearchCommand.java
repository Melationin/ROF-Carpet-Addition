package com.carpet.rof.commands;

import carpet.CarpetServer;
import carpet.api.settings.*;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.utils.ROFCommandHelper;
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

import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.utils.Translations.tr;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@ROFRule
@ROFCommand
public class SearchCommand
{

    @Rule(categories = {ROF, COMMAND}, strict = false, validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "Carpet规则搜索命令",
            description = "添加了carpet的子命令search，可以通过关键字搜索carpet规则")
    public static String commandRulesSearcher = "true";
    private static final String IDENTIFIER = "carpet";

    private static Text makeSetRuleButton(CarpetRule<?> rule, String option)
    {
        String style = RuleHelper.isInDefaultValue(rule) ? "g" : (option.equalsIgnoreCase(
                RuleHelper.toRuleString(rule.defaultValue())) ? "e" : "y");
        if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.value()))) {
            style = style + "u";
            if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.defaultValue())))
                style = style + "b";
        }
        String component = style + " [" + option + "]";
        if (option.equalsIgnoreCase(RuleHelper.toRuleString(rule.value())))
            return Messenger.c(component);

        return Messenger.c(component, "^g " + tr(TranslationKeys.SWITCH_TO).formatted(
                        option + (option.equals(RuleHelper.toRuleString(rule.defaultValue())) ? " (default)" : "")),
                "?/" + IDENTIFIER + " " + rule.name() + " " + option);
    }

    private static Text displayInteractiveSetting(CarpetRule<?> rule)
    {
        String displayName = RuleHelper.translatedName(rule);
        List<Object> args = new ArrayList<>();
        args.add("w - " + displayName + " ");
        args.add("!/" + IDENTIFIER + " " + rule.name());
        args.add("^y " + RuleHelper.translatedDescription(rule));
        for (String option : rule.suggestions()) {
            args.add(makeSetRuleButton(rule, option));
            args.add("w  ");
        }
        if (!rule.suggestions().contains(RuleHelper.toRuleString(rule.value()))) {
            args.add(makeSetRuleButton(rule, RuleHelper.toRuleString(rule.value())));
            args.add("w  ");
        }
        args.removeLast();
        return Messenger.c(args.toArray(new Object[0]));
    }

    public static int executeCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        if(context.getSource().getPlayer() == null){
            context.getSource().sendFeedback(() -> Text.of("This command can only be used by players."), false);
            return 0;
        }

        String key = StringArgumentType.getString( context, "key");
        boolean enable = ROFCommandHelper.getArgumentOrDefault(context, "isNoDefaultValue", false,
                BoolArgumentType::getBool);

        String category = ROFCommandHelper.getArgumentOrDefault(context, "category", null,
                StringArgumentType::getString);

        ServerCommandSource source = context.getSource();

        SettingsManager manager = CarpetServer.settingsManager;
        Messenger.m(source, " ");
        Messenger.m(source, "wb 搜索结果为: ");
        int i = 0;
        for (var c : manager.getCarpetRules()) {
            String allText = c.name() + RuleHelper.translatedDescription(c);
            if (allText.toLowerCase().contains(key.toLowerCase()) && (category == null || c.categories()
                    .contains(category)) && (!enable || !RuleHelper.isInDefaultValue(c))) {
                Messenger.m(source, displayInteractiveSetting(c));
                i++;
            }
        }
        Messenger.m(source, "wb 一共搜索到" + i + "条规则");
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {


        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot().getChild(IDENTIFIER));


        helper.registerCommand("search{r} <key> [isNoDefaultValue] [category]{s}")
                .rCarpet(()->commandRulesSearcher)
                .arg(StringArgumentType.string())
                .arg(BoolArgumentType.bool())
                .arg( StringArgumentType.string())
                .s(new CategoriesSuggestionProvider())
                .command(SearchCommand::executeCommand);
    }

    public static class CategoriesSuggestionProvider implements SuggestionProvider<ServerCommandSource>
    {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
        {

            for (String str : CarpetServer.settingsManager.getCategories()) {
                builder.suggest(str);
            }

            return builder.buildFuture();
        }
    }
}