package com.carpet.rof.utils;

import carpet.api.settings.Rule;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.ROFSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class RofCarpetTranslations
{
    public static Map<String, String> getTranslationFromResourcePath(String lang)
    {
    	InputStream langFile = RofCarpetTranslations.class.getClassLoader().getResourceAsStream("assets/RofCarpetAddition/lang/%s.json".formatted(lang));
        if (langFile == null) {
            // we don't have that language
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().create(); // lenient is now default behavior
        Map<String, String> tempMap =  gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());

        var classes = ROFSettings.ruleClasses;
        for (var clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                String n1 = "carpet.rule." + field.getName();
                if(field.isAnnotationPresent(Rule.class)) {
                    if (!tempMap.containsKey(n1 + ".desc") && lang.equals("en_us")) {
                        tempMap.put(n1 + ".desc", "");
                    }
                    if (field.isAnnotationPresent(QuickTranslations.class)) {
                        QuickTranslations translations = field.getAnnotation(QuickTranslations.class);
                        if (translations.lang().equals(lang)) {
                            if (!tempMap.containsKey(n1 + ".desc")) {
                                tempMap.put(n1 + ".desc", translations.description());
                            }
                            if (!tempMap.containsKey(n1 + ".name")) {
                                tempMap.put(n1 + ".name", translations.name());
                            }
                            if (!tempMap.containsKey(n1 + ".extra")) {
                                for (int i = 0; i < translations.extra().length; i++) {
                                    tempMap.put(n1 + ".extra." + i, translations.extra()[i]);
                                }
                            }
                        }
                    }
                }
            }
        }


        return tempMap;

    }
}
