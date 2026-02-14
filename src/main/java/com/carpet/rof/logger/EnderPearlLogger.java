package com.carpet.rof.logger;

import carpet.logging.Logger;
import com.carpet.rof.annotation.ROFLogger;

import java.lang.reflect.Field;

@ROFLogger
public class EnderPearlLogger extends Logger
{

    public static boolean enderPearl = true;


    public EnderPearlLogger(Field acceleratorField, String logName, String def, String[] options, boolean strictOptions)
    {
        super(acceleratorField, logName, def, options, strictOptions);
    }


    public static Logger create()
    {
        try {
            return new EnderPearlLogger(EnderPearlLogger.class.getField("enderPearl"),"enderPearl","self",
                    new String[]{"self", "all"},true);
        }
        catch (Exception ignored){}
        return null;
    }
}
