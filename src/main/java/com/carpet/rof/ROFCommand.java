package com.carpet.rof;

import com.carpet.rof.annotation.CarpetCommand;
import com.carpet.rof.annotation.RulesSetting;
import com.carpet.rof.commands.AutoFreezeCommand;
import com.carpet.rof.commands.HCLCommand;
import com.carpet.rof.commands.PlayerJScriptCommand;
import com.carpet.rof.commands.SearchCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


public class ROFCommand {
    static List<Consumer<CommandDispatcher<ServerCommandSource>>> commandClasses = new ArrayList<>();
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext){

        commandClasses.add(AutoFreezeCommand::registerDispatcher);
        commandClasses.add(HCLCommand::registerDispatcher);
        commandClasses.add(SearchCommand::registerDispatcher);
        commandClasses.add(PlayerJScriptCommand::registerDispatcher);
        for(var c : commandClasses){
           c.accept(dispatcher);
        }
    }


}
