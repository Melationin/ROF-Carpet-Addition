package com.carpet.rof;

import com.carpet.rof.annotation.CarpetCommand;
import com.carpet.rof.annotation.RulesSetting;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.carpet.rof.generated.CommandRegistry.CLASS_NAMES;

public class RofCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        for (String className :CLASS_NAMES) {
            try {
                Class<?> cls = Class.forName(className);
                if (cls.isAnnotationPresent(CarpetCommand.class)) {
                    if(cls.getAnnotation(CarpetCommand.class).enabled()){
                        Method m = cls.getDeclaredMethod("register",CommandDispatcher.class);

                        if (!Modifier.isStatic(m.getModifiers())) {
                            throw new IllegalStateException(
                                    cls.getName() + ".register() must be static"
                            );
                        }

                        m.setAccessible(true); // 防止非 public

                        m.invoke(null,dispatcher);
                    };
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                        "Generated command class not found: " + className, e
                );
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
