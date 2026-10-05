package com.carpet.rof.command;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import carpet.utils.Translations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.carpet.composite.CompositeRuleManager;
import com.carpet.rof.carpet.composite.CompositeRuleValidator;
import com.carpet.rof.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@ROFCommand
public final class CompositeRuleCommand
{
    private CompositeRuleCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        var manager = CarpetServer.settingsManager;
        var root = dispatcher.getRoot().getChild(manager.identifier());
        if (root == null) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(root);
        helper.registerCommand("group{r}")
                .r(source -> !manager.locked() && carpet.utils.CommandHelper.canUseCommand(
                        source, CarpetSettings.carpetCommandPermissionLevel))
                .command(context -> {
                    Messenger.m(context.getSource(), "wb 复合规则:");
                    for (CompositeRuleValidator rule : CompositeRuleManager.of(manager).all()) {
                        Messenger.m(context.getSource(), "w - " + RuleHelper.translatedName(rule.parent()),
                                "!" + path(rule), "^y " + RuleHelper.translatedDescription(rule.parent()));
                    }
                    return 1;
                });

        helper.registerCommand("group <groupName>{s}")
                .arg(StringArgumentType.word())
                .s((context, builder) -> SharedSuggestionProvider.suggest(
                        CompositeRuleManager.of(manager).all().stream().map(group -> group.parent().name()), builder))
                .command(context -> {
                    CompositeRuleValidator rule = rule(context);
                    Messenger.m(context.getSource(), "");
                    Messenger.m(context.getSource(), "wb " + RuleHelper.translatedName(rule.parent()),
                            "!" + path(rule), "^g refresh");
                    Messenger.m(context.getSource(), "w " + RuleHelper.translatedDescription(rule.parent()));
                    for (String name : rule.memberNames()) {
                        Messenger.m(context.getSource(), memberRow(rule, name));
                    }
                    return 1;
                });

        helper.registerCommand("group <groupName> <member>{s}")
                .arg(StringArgumentType.word())
                .arg(StringArgumentType.word())
                .s((context, builder) -> SharedSuggestionProvider.suggest(rule(context).memberNames(), builder))
                .command(context -> {
                    CompositeRuleValidator rule = rule(context);
                    CarpetRule<?> member = member(context, rule);
                    Messenger.m(context.getSource(), "");
                    Messenger.m(context.getSource(), "wb " + translatedName(rule, member.name()),
                            "!" + path(rule) + " " + member.name(), "^g refresh");
                    Messenger.m(context.getSource(), "g 父规则: ", "c [" + RuleHelper.translatedName(rule.parent()) + "]",
                            "!" + path(rule), "^y " + RuleHelper.translatedDescription(rule.parent()));
                    Messenger.m(context.getSource(), "w " + description(rule, member.name()));
                    for (Component info : member.extraInfo()) Messenger.m(context.getSource(), info);
                    Messenger.m(context.getSource(), "w " + Translations.tr(TranslationKeys.CURRENT_VALUE) + ": ",
                            (RuleHelper.getBooleanValue(member) ? "lb " : "nb ") + RuleHelper.toRuleString(member.value())
                                    + " (" + (RuleHelper.isInDefaultValue(member) ? "default" : "modified") + " value)");
                    List<Object> options = new ArrayList<>(List.of("w Options: ", "y [ "));
                    for (String option : member.suggestions()) {
                        options.add(optionButton(rule, member, option, false));
                        options.add("w  ");
                    }
                    if (!member.suggestions().isEmpty()) options.removeLast();
                    options.add("y  ]");
                    Messenger.m(context.getSource(), options.toArray());
                    return 1;
                });

        helper.registerCommand("group <groupName> <member> <value>{s}")
                .arg(StringArgumentType.word())
                .arg(StringArgumentType.word())
                .arg(StringArgumentType.greedyString())
                .s((context, builder) -> SharedSuggestionProvider.suggest(
                        member(context, rule(context)).suggestions(), builder))
                .command(context -> {
                    CompositeRuleValidator rule = rule(context);
                    CarpetRule<?> member = member(context, rule);
                    return rule.saveMember(dispatcher, context.getSource(), member.name(),
                            StringArgumentType.getString(context, "value"));
                });
    }

    private static CompositeRuleValidator rule(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException
    {
        var rule = CompositeRuleManager.of(CarpetServer.settingsManager)
                .get(StringArgumentType.getString(context, "groupName"));
        if (rule != null) return rule;
        throw new SimpleCommandExceptionType(Component.literal("未知复合规则")).create();
    }

    private static CarpetRule<?> member(CommandContext<CommandSourceStack> context, CompositeRuleValidator rule)
            throws CommandSyntaxException
    {
        try {
            return rule.member(StringArgumentType.getString(context, "member"));
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(Component.literal(e.getMessage())).create();
        }
    }

    private static String path(CompositeRuleValidator rule)
    {
        return "/" + rule.parent().settingsManager().identifier() + " group " + rule.parent().name();
    }

    private static String description(CompositeRuleValidator group, String name)
    {
        String key = group.parent().settingsManager().identifier() + ".rule." + group.parent().name() + "." + name;
        return Translations.tr(key + ".desc", RuleHelper.translatedDescription(group.member(name)));
    }

    private static String translatedName(CompositeRuleValidator group, String name)
    {
        String key = group.parent().settingsManager().identifier() + ".rule." + group.parent().name() + "." + name;
        return Translations.hasTranslation(key + ".name") ? Translations.tr(key + ".name") + " (" + name + ")" : name;
    }

    private static Component memberRow(CompositeRuleValidator group, String name)
    {
        CarpetRule<?> rule = group.member(name);
        List<Object> parts = new ArrayList<>(List.of("w - " + translatedName(group, name) + " ",
                "!" + path(group) + " " + name, "^y " + description(group, name)));
        for (String option : rule.suggestions()) {
            parts.add(optionButton(group, rule, option, true));
            parts.add("w  ");
        }
        String current = RuleHelper.toRuleString(rule.value());
        if (!rule.suggestions().contains(current)) parts.add(optionButton(group, rule, current, true));
        return Messenger.c(parts.toArray());
    }

    private static Component optionButton(CompositeRuleValidator group, CarpetRule<?> rule,
                                          String option, boolean brackets)
    {
        String current = RuleHelper.toRuleString(rule.value());
        String defaults = RuleHelper.toRuleString(rule.defaultValue());
        String style = RuleHelper.isInDefaultValue(rule) ? "g" : option.equalsIgnoreCase(defaults) ? "e" : "y";
        if (option.equalsIgnoreCase(current)) {
            style += "u";
            if (option.equalsIgnoreCase(defaults)) style += "b";
        }
        String label = style + (brackets ? " [" : " ") + option + (brackets ? "]" : "");
        if (option.equalsIgnoreCase(current)) return Messenger.c(label);
        return Messenger.c(label, "^g " + Translations.tr(TranslationKeys.SWITCH_TO).formatted(
                        option + (option.equals(defaults) ? " (default)" : "")),
                "?" + path(group) + " " + rule.name() + " " + option);
    }
}
