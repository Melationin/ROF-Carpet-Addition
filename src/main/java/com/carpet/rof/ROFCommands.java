package com.carpet.rof;

import com.carpet.rof.annotation.ROFCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.carpet.rof.generated.CommandList.CLASS_NAMES;

public class ROFCommands
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        for (String className : CLASS_NAMES) {
            try {
                Class<?> cls = Class.forName(className);
                Method m = cls.getDeclaredMethod("register", CommandDispatcher.class);
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new IllegalStateException(cls.getName() + ".register() must be static");
                }
                m.setAccessible(true);
                m.invoke(null, dispatcher);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Generated registerCommand class not found: " + className, e);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
