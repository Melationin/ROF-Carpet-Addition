package com.carpet.rof;

import com.carpet.rof.annotation.RulesSetting;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.carpet.rof.generated.SettingRegistry.CLASS_NAMES;

public class Settings {
    public static List<Class<?>> ruleClasses = new ArrayList<>();

    public static void loadClasses() {
        for (String className :CLASS_NAMES) {
            try {
                Class<?> cls = Class.forName(className);
                ruleClasses.add(cls);

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                        "Generated rule class not found: " + className, e
                );
            }
        }
    }

}
