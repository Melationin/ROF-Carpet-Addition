package com.carpet.rof.logger;

import carpet.logging.Logger;
import com.carpet.rof.annotation.ROFLogger;

import java.lang.reflect.Field;

@ROFLogger
public class ProjectileTraker extends Logger
{
    public static final String NAME = "projectileTraker";

    public static boolean projectileTraker = true;



    public ProjectileTraker(Field acceleratorField, String logName, String def, String[] options, boolean strictOptions)
    {
        super(acceleratorField, logName, def, options, strictOptions);
    }


    public static Logger create()
    {
        try {
            return new ProjectileTraker(ProjectileTraker.class.getField(NAME),NAME,"self",
                    new String[]{"self", "all","selfAuto"},true);
        }
        catch (Exception ignored){}
        return null;
    }
}
