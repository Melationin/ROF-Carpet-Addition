package com.carpet.rof;

import carpet.CarpetSettings;

import carpet.api.settings.Rule;

import com.carpet.rof.utils.RofCarpetTranslations;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Dosc
{
    private static final Map<String, RuleData<?>> rules = new HashMap<>();

    public static class RuleData<S>
    {
        public String name;
        public Collection<String> categories;
        public Collection<String> suggestions;
        public S defaultValue;
        public boolean strict;
        public Class<S> type;

        public RuleData(Field field){
            carpet.api.settings.Rule a = field.getAnnotation(carpet.api.settings.Rule.class);
            this.name = field.getName();
            this.categories = List.of(a.categories());
            this.suggestions = List.of(a.options());
            try {
                this.defaultValue = (S) field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            this.strict = a.strict();
            this.type = (Class<S>) field.getType();
        }
    }


    public static void parseSettingsClass(Class<?> settingsClass)
    {
        // In the current translation system languages are not loaded this early. Ensure they are loaded
       //Translations.updateLanguage();
        boolean warned = settingsClass == CarpetSettings.class; // don't warn for ourselves

        nextRule: for (Field field : settingsClass.getDeclaredFields())
        {
            Class<? extends Rule.Condition>[] conditions;
            Rule newAnnotation = field.getAnnotation(Rule.class);

            if (newAnnotation != null) {
                conditions = newAnnotation.conditions();
            }else {
                continue;
            }
            for (Class<? extends Rule.Condition> condition : conditions) { //Should this be moved to ParsedRule.of?
                try
                {
                    Constructor<? extends Rule.Condition> constr = condition.getDeclaredConstructor();
                    constr.setAccessible(true);
                    if (!(constr.newInstance()).shouldRegister())
                        continue nextRule;
                }
                catch (ReflectiveOperationException e)
                {
                    throw new IllegalArgumentException(e);
                }
            }
            var parsed = new RuleData(field);
            rules.put(parsed.name,  parsed);
        }
    }


    public static String generateDocument(List<RuleData<?>> rules, Map<String, String> translations){
        StringBuilder sb = new StringBuilder();
        // 文档头部
        sb.append("# 规则\n\n");
        sb.append("**提示：可以使用`Ctrl+F`快速查找自己想要的规则**\n\n");

        for (RuleData<?> rule : rules) {

            String trP = "carpet.rule." + rule.name + ".";

            String ruleNameTr = translations.getOrDefault(trP+ "name", rule.name);
            if(ruleNameTr.equals(rule.name)){
                sb.append("## ").append(ruleNameTr);
            }else {
                sb.append("## ").append(ruleNameTr).append(" (").append(rule.name).append(")");
            }

            sb.append("\n\n");

            // 描述（可能多行，直接输出，注意保持原样）
            sb.append("&emsp;").append(translations.getOrDefault(trP+ "desc","[ERROR]")).append("\n\n");

            if(!rule.name.startsWith("command")) {
                int i = 0;
                while (translations.containsKey(trP + "extra." + i)) {
                    sb.append("&emsp;").append(translations.get(trP + "extra." + i)).append("\n\n");
                    i++;
                }
            }

            // 类型
            sb.append("&emsp;- 类型: `").append(rule.type.getSimpleName()).append("`\n\n");

            // 默认值
            sb.append("&emsp;- 默认值: `").append(rule.defaultValue).append("`\n\n");

            // 参考选项（如果存在且非空）
            if (rule.suggestions != null && !rule.suggestions.isEmpty()) {
                String options = rule.suggestions.stream()
                        .map(opt -> "`" + opt + "`")
                        .collect(Collectors.joining(", "));
                sb.append("&emsp;- 参考选项: ").append(options).append("\n\n");
            }

            // 分类
            String categories = rule.categories.stream()
                    .map(cat -> "`" + cat + "`")
                    .collect(Collectors.joining(", "));
            sb.append("&emsp;").append("- 分类: ").append(categories).append("\n\n");

            // 规则之间空一行
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String generateCommand(List<RuleData<?>> rules, Map<String, String> translations){
        StringBuilder sb = new StringBuilder();
        // 文档头部
        sb.append("# 命令\n\n");
        sb.append("**提示：可以使用`Ctrl+F`快速查找自己想要的命令**\n\n");

        for (RuleData<?> rule : rules) {

            if(!rule.name.startsWith("command")) { continue;}
            String trP = "carpet.rule." + rule.name + ".";

            String ruleNameTr = translations.getOrDefault(trP+ "name", rule.name);
            if(ruleNameTr.equals(rule.name)){
                sb.append("## ").append(ruleNameTr);
            }else {
                sb.append("## ").append(ruleNameTr).append(" (").append(rule.name).append(")");
            }

            sb.append("\n\n");

            // 描述（可能多行，直接输出，注意保持原样）
            sb.append("&emsp;").append(translations.getOrDefault(trP+ "desc","[ERROR]")).append("\n\n");
            sb.append("### &emsp;用法:\n\n");
            int i = 0;
            while (translations.containsKey(trP + "extra." + i)) {
                sb.append("&emsp;&emsp;- ").append(translations.get(trP + "extra." + i)).append("\n\n");
                i++;
            }

            // 规则之间空一行
            sb.append("\n");
        }

        return sb.toString();
    }




    public static void saveStringToFile(String content, String filePath) throws IOException
    {
        Files.writeString(Path.of(filePath), content);
    }


    public static void main2(String[] args)
    {
        ROFSettings.loadClasses();

        for(var clazz :  ROFSettings.ruleClasses){
            parseSettingsClass(clazz);
        }

        Map<String,String> tr =RofCarpetTranslations.getTranslationFromResourcePath("zh_cn");

        List<RuleData<?>> sortRules = new ArrayList<>(rules.values());
        sortRules.sort(Comparator.comparing(r -> ((RuleData<?>)r).name));

        String doc = generateDocument(sortRules, tr);
        try {
            saveStringToFile(doc, "docs/rules.md");
            //System.out.println("文档已成功保存到 carpet_rules.md");
        } catch (IOException e) {
            //System.err.println("保存文档时出错：" + e.getMessage());
            e.printStackTrace();
        }

        String docC = generateCommand(sortRules, tr);
        try {
            saveStringToFile(docC, "docs/command.md");
           // System.out.println("文档已成功保存到 carpet_rules.md");
        } catch (IOException e) {
            //System.err.println("保存文档时出错：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
