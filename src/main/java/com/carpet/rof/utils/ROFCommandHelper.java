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
import com.mojang.logging.LogUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.carpet.rof.utils.RofTool.rDEBUG;

public class ROFCommandHelper <S extends CommandSource>
{

    /*
    输入格式 xxx <arg1> <arg2>{} xx [arg3]{} [arg4]

    {}为附加参数
     */
    public static class Reused{}

    public static Reused reused(){return new Reused();}

    final CommandNode<S> rootNode;

    public Predicate<S> predicate(Predicate<S> predicate){
        return predicate;
    };

    public ROFCommandHelper(CommandNode<S> rootNode)
    {
        this.rootNode = rootNode;
    }

    protected static class CommandArg{
        String name;
        public enum CommandArgTYPE{
            NORMAl,
            Argument,
            OptionalArg
        }

        public CommandArgTYPE type;
        public List<Object> otherArg = new ArrayList<>();
    }


    private  void command(CommandNode<S> commandNode, List<CommandArg> commandArgs, Command<S> execute)
    {
        if(commandArgs.isEmpty()){
            try {
                Field commandField = CommandNode.class.getDeclaredField("command");
                commandField.setAccessible(true);
                commandField.set(commandNode,execute);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        //rDEBUG("---------------");

        var commandArg = commandArgs.get(0);
        var child = commandNode.getChild(commandArg.name);
        if(child == null){
            ArgumentBuilder<S,?> argBuilder;
            if(commandArg.type == CommandArg.CommandArgTYPE.Argument || commandArg.type == CommandArg.CommandArgTYPE.OptionalArg){
                RequiredArgumentBuilder<S,?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(commandArg.name,(ArgumentType<?>)commandArg.otherArg.getFirst());
                for(Object o: commandArg.otherArg.subList(1,commandArg.otherArg.size())){

                    rDEBUG(o.getClass().getSimpleName());
                    if(o instanceof SuggestionProvider<?> suggestionProvider){
                        requiredArgumentBuilder.suggests((SuggestionProvider<S>) suggestionProvider);
                       // rDEBUG("suggestionProvider");

                    }else if(o instanceof Predicate<?> predicate){
                        requiredArgumentBuilder.requires((Predicate<S>) predicate);
                        //rDEBUG("predicate");
                    }
                }
                argBuilder = requiredArgumentBuilder;
            }else {
                argBuilder = LiteralArgumentBuilder.literal(commandArg.name);
                for(Object o: commandArg.otherArg){
                    if(o instanceof Predicate<?> predicate){
                        argBuilder.requires((Predicate<S>) predicate);
                    }
                }
            }
            commandNode.addChild(argBuilder.build());
            child = commandNode.getChild(commandArg.name);
        }

        if(commandArg.type != CommandArg.CommandArgTYPE.OptionalArg){
            command(child, commandArgs.subList(1,commandArgs.size()),execute);
        }else {
            command(child, commandArgs.subList(1,commandArgs.size()), execute);
            command(commandNode, commandArgs.subList(1,commandArgs.size()),execute);
        }
    };

    public static <S> boolean hasArgument(CommandContext<S> ctx, String arg){
        try {
            Field arguments = CommandContext.class.getDeclaredField("arguments");
            arguments.setAccessible(true);
            return ((Map<String, ParsedArgument<S, ?>>)(arguments.get(ctx))).containsKey(arg);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            LogUtils.getLogger().error(e.getMessage());
        }
        return false;
    }

    public static <S extends CommandSource> Predicate<S> carpetRequire(String ruleName){
        return (S source)->carpet.utils.CommandHelper.canUseCommand((ServerCommandSource) source,ruleName);
    }
    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws CommandSyntaxException; // 或其他特定异常/泛型异常
    }
    public static <S extends CommandSource, T> T getArgumentOrDefault(CommandContext<S> ctx, String arg, T defaultValue, ThrowingBiFunction<CommandContext<S>, String, T> function) throws CommandSyntaxException
    {

        try {
            if (hasArgument(ctx, arg)) {
                return function.apply(ctx, arg);
            } else {
                return defaultValue;
            }
        }catch (Exception ignored){}
        return defaultValue;
    }

    public  void registerCommand(String commandString, Command<S> execute, Object... args){
        final Pattern MAIN =
                Pattern.compile("^(?:\\[([^]]+)]|<([^>]+)>|([^\\[{<]+))((?:\\{[^}]+})*)$");

        //解析command
        List<CommandArg> commandArgs = new ArrayList<>();
        var tokenList = commandString.split(" ");
        int index = 0;

        for(var token : tokenList){
            if(!token.isEmpty()){
                CommandArg commandArg = new CommandArg();
                Matcher mainMatcher = MAIN.matcher(token);
                if (!mainMatcher.find()) {
                    throw new IllegalArgumentException("Invalid head: " + token);
                }
                if(mainMatcher.group(1)!= null){
                    commandArg.name = mainMatcher.group(1);
                    commandArg.type = CommandArg.CommandArgTYPE.OptionalArg;
                }else if(mainMatcher.group(2)!= null){
                    commandArg.name = mainMatcher.group(2);
                    commandArg.type = CommandArg.CommandArgTYPE.Argument;
                }
                else if(mainMatcher.group(3)!= null){
                    commandArg.name = mainMatcher.group(3);
                    commandArg.type = CommandArg.CommandArgTYPE.NORMAl;
                }else throw new IllegalArgumentException("Invalid head: " + token);

                List<String> other = new ArrayList<>();
                String suffixes = mainMatcher.group(4);
                if (suffixes != null && !suffixes.isEmpty()) {
                    // 第二步：提取所有后缀
                    Pattern suffixPattern = Pattern.compile("\\{([^}]+)}");
                    Matcher suffixMatcher = suffixPattern.matcher(suffixes);
                    while (suffixMatcher.find()) {
                        other.add(suffixMatcher.group(1));
                    }
                }
                if(commandArg.type != CommandArg.CommandArgTYPE.NORMAl){
                    if(args[index] instanceof ArgumentType<?> || args[index] instanceof Reused){
                        commandArg.otherArg.add(args[index]);
                        index++;
                    }else throw new IllegalArgumentException("Invalid Additional Parameters:  " + token);
                }
                for(var it: other){
                    if(it.equals("r")){
                        if(args[index] instanceof Predicate<?>){
                            commandArg.otherArg.add(args[index]);
                            //rDEBUG(" PredicateSucc");
                            index++;
                        }else throw new IllegalArgumentException("Invalid Additional Parameters:  " + token);
                    }else if(it.equals("s")){
                        if(args[index] instanceof SuggestionProvider<?> ){
                            commandArg.otherArg.add(args[index]);
                           // rDEBUG("suggestionProviderSucc");
                            index++;
                        }else throw new IllegalArgumentException("Invalid Additional Parameters:  " + token);
                    }else throw new IllegalArgumentException("Invalid Additional Parameters:  " + token);
                }
                commandArgs.add(commandArg);
            }
        }

       command(rootNode,commandArgs,execute);
    }
}
