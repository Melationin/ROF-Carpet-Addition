package com.carpet.rof.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.CheckReturnValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ROFCommandHelper <S extends CommandSource>
{

    /*
    输入格式 xxx <arg1> <arg2>{} xx [arg3]{} [arg4]

    {}为附加参数
     */
    public static class Reused{}

    public static Reused reused(){return new Reused();}

    final CommandNode<S> rootNode;

    public Predicate<S> predicate(Predicate<S> predicate) {
        return predicate;
    }

    public ROFCommandHelper(CommandNode<S> rootNode)
    {
        this.rootNode = rootNode;
    }

    protected static class CommandArg {
        String name;

        public enum CommandArgType {
            NORMAL,
            ARGUMENT,
            OPTIONAL_ARG
        }

        public CommandArgType type;
        public List<Object> otherArg = new ArrayList<>();
    }


    private static final Pattern MAIN_PATTERN =
            Pattern.compile("^(?:\\[([^]]+)]|<([^>]+)>|([^\\[{<]+))((?:\\{[^}]+})*)$");
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("\\{([^}]+)}");

    @SuppressWarnings("unchecked")
    private void command(CommandNode<S> commandNode, List<CommandArg> commandArgs, Command<S> execute)
    {
        if (commandArgs.isEmpty()) {
            try {
                Field commandField = CommandNode.class.getDeclaredField("command");
                commandField.setAccessible(true);
                commandField.set(commandNode, execute);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        var commandArg = commandArgs.getFirst();
        var child = commandNode.getChild(commandArg.name);
        if (child == null) {
            ArgumentBuilder<S, ?> argBuilder;
            if (commandArg.type == CommandArg.CommandArgType.ARGUMENT
                    || commandArg.type == CommandArg.CommandArgType.OPTIONAL_ARG) {
                RequiredArgumentBuilder<S, ?> requiredArgumentBuilder =
                        RequiredArgumentBuilder.argument(commandArg.name, (ArgumentType<?>) commandArg.otherArg.getFirst());
                for (Object o : commandArg.otherArg.subList(1, commandArg.otherArg.size())) {
                    if (o instanceof SuggestionProvider<?> suggestionProvider) {
                        requiredArgumentBuilder.suggests((SuggestionProvider<S>) suggestionProvider);
                    } else if (o instanceof Predicate<?> predicate) {
                        requiredArgumentBuilder.requires((Predicate<S>) predicate);
                    }
                }
                argBuilder = requiredArgumentBuilder;
            } else {
                argBuilder = LiteralArgumentBuilder.literal(commandArg.name);
                for (Object o : commandArg.otherArg) {
                    if (o instanceof Predicate<?> predicate) {
                        argBuilder.requires((Predicate<S>) predicate);
                    }
                }
            }
            commandNode.addChild(argBuilder.build());
            child = commandNode.getChild(commandArg.name);
        }

        if (commandArg.type != CommandArg.CommandArgType.OPTIONAL_ARG) {
            command(child, commandArgs.subList(1, commandArgs.size()), execute);
        } else {
            command(child, commandArgs.subList(1, commandArgs.size()), execute);
            command(commandNode, commandArgs.subList(1, commandArgs.size()), execute);
        }
    }

    @SuppressWarnings("unchecked")
    public static <S> boolean hasArgument(CommandContext<S> ctx, String arg) {
        try {
            Field arguments = CommandContext.class.getDeclaredField("arguments");
            arguments.setAccessible(true);
            return ((Map<String, ParsedArgument<S, ?>>) arguments.get(ctx)).containsKey(arg);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return false;
    }

    public static <S extends CommandSource> Predicate<S> carpetRequire(Supplier<String> ruleName) {
        return source -> carpet.utils.CommandHelper.canUseCommand((ServerCommandSource) source, ruleName.get());
    }

    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws CommandSyntaxException;
    }

    public static <S extends CommandSource, T> T getArgumentOrDefault(
            CommandContext<S> ctx, String arg, T defaultValue,
            ThrowingBiFunction<CommandContext<S>, String, T> function)
    {
        try {
            if (hasArgument(ctx, arg)) {
                return function.apply(ctx, arg);
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public void registerCommand(String commandString, Command<S> execute, Object... args) {
        registerCommandList(commandString, execute, List.of(args));
    }

    protected void registerCommandList(String commandString, Command<S> execute, List<Object> args) {
        List<CommandArg> commandArgs = new ArrayList<>();
        int index = 0;
        try {
            for (String token : commandString.split(" ")) {
                if (token.isEmpty())
                    continue;

                CommandArg commandArg = new CommandArg();
                Matcher mainMatcher = MAIN_PATTERN.matcher(token);
                if (!mainMatcher.find()) {
                    throw new IllegalArgumentException("Invalid token: " + token);
                }
                if (mainMatcher.group(1) != null) {
                    commandArg.name = mainMatcher.group(1);
                    commandArg.type = CommandArg.CommandArgType.OPTIONAL_ARG;
                } else if (mainMatcher.group(2) != null) {
                    commandArg.name = mainMatcher.group(2);
                    commandArg.type = CommandArg.CommandArgType.ARGUMENT;
                } else if (mainMatcher.group(3) != null) {
                    commandArg.name = mainMatcher.group(3);
                    commandArg.type = CommandArg.CommandArgType.NORMAL;
                } else {
                    throw new IllegalArgumentException("Invalid token: " + token);
                }

                List<String> suffixKeys = new ArrayList<>();
                String suffixes = mainMatcher.group(4);
                if (suffixes != null && !suffixes.isEmpty()) {
                    Matcher suffixMatcher = SUFFIX_PATTERN.matcher(suffixes);
                    while (suffixMatcher.find()) {
                        suffixKeys.add(suffixMatcher.group(1));
                    }
                }

                if (commandArg.type != CommandArg.CommandArgType.NORMAL) {
                    if (args.get(index) instanceof ArgumentType<?> || args.get(index) instanceof Reused) {
                        commandArg.otherArg.add(args.get(index));
                        index++;
                    } else {
                        throw new IllegalArgumentException("Invalid Additional Parameters: " + token);
                    }
                }

                for (String key : suffixKeys) {
                    switch (key) {
                        case "r" -> {
                            if (args.get(index) instanceof Predicate<?>) {
                                commandArg.otherArg.add(args.get(index++));
                            } else
                                throw new IllegalArgumentException("Invalid Additional Parameters: " + token);
                        }
                        case "s" -> {
                            if (args.get(index) instanceof SuggestionProvider<?>) {
                                commandArg.otherArg.add(args.get(index++));
                            } else
                                throw new IllegalArgumentException("Invalid Additional Parameters: " + token);
                        }
                        default -> throw new IllegalArgumentException("Invalid Additional Parameters: " + token);
                    }
                }
                commandArgs.add(commandArg);
            }

            command(rootNode, commandArgs, execute);
        }catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not Enough Additional Parameters for command: " + commandString, e);
        }
    }



    public    class commandBuilder{

        String string;
        List<Object> objects = new ArrayList<>();

        public commandBuilder(String string)
        {
            this.string = string;
        }

        //@CheckReturnValue
        public commandBuilder r(Predicate<S> predicate){
            objects.add(predicate);
            return this;
        }

        //@CheckReturnValue
        public commandBuilder rCarpet(Supplier<String> ruleName){
            return r(source -> carpet.utils.CommandHelper.canUseCommand((ServerCommandSource) source, ruleName.get()));
        }

        @CheckReturnValue
        public commandBuilder s(SuggestionProvider<S> suggestionProvider){
            objects.add(suggestionProvider);
            return this;
        }

        @CheckReturnValue
        public commandBuilder arg(ArgumentType<?> argumentType){
            objects.add(argumentType);
            return this;
        }

        public void command(Command<S> command){


            ROFCommandHelper.this.registerCommandList(string, command,objects);
        }

    }

    @CheckReturnValue
    public commandBuilder registerCommand(String commandString) {
      return new commandBuilder(commandString);
    }
}
