package com.carpet.rof;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.carpet.rof.annotation.ROFCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.carpet.rof.generated.LoggerList;

public class ROFLoggers
{

    public static List<Logger> loggers = new ArrayList<Logger>();
    public static void register()
    {
        for(String className : LoggerList.CLASS_NAMES){
            try {
                Class<?> cls = Class.forName(className);
                Logger logger =  (Logger) (cls.getMethod("create").invoke(null));
                LoggerRegistry.registerLogger(logger.getLogName(), logger);

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Generated rule class not found: " + className, e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
