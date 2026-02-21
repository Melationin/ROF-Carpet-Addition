package com.carpet.rof;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import com.carpet.rof.generated.LoggerList;

public class ROFLoggers
{
    public static void register()
    {
        for (String className : LoggerList.CLASS_NAMES) {
            try {
                Class<?> cls = Class.forName(className);
                Logger logger = (Logger) cls.getMethod("create").invoke(null);
                LoggerRegistry.registerLogger(logger.getLogName(), logger);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Generated logger class not found: " + className, e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
