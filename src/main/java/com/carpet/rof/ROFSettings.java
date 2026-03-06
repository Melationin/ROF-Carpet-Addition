package com.carpet.rof;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import com.carpet.rof.generated.SettingList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class ROFSettings
{
    public static List<Class<?>> ruleClasses = Collections.emptyList();

    public static void loadClasses()
    {
        List<Class<?>> classes = new ArrayList<>();
        for (String className : SettingList.CLASS_NAMES) {
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Generated rule class not found: " + className, e);
            }
        }
        ruleClasses = Collections.unmodifiableList(classes);
    }

}
