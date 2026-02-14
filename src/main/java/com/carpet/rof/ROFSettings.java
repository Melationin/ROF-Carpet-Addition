package com.carpet.rof;

import com.carpet.rof.generated.SettingList;

import java.util.ArrayList;
import java.util.List;


public class ROFSettings
{
    public static List<Class<?>> ruleClasses = new ArrayList<>();

    public static void loadClasses()
    {
        for (String className : SettingList.CLASS_NAMES) {
            try {
                Class<?> cls = Class.forName(className);
                ruleClasses.add(cls);

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Generated rule class not found: " + className, e);
            }
        }
    }

}
