package com.carpet.rof;

import javax.annotation.processing.Processor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class Settings {
    public static List<Class<?>> ruleClasses = new ArrayList<>();

    public static void scanAndRegister(String basePackage) {
        try {
            // 将包路径转换为文件路径
            String packagePath = basePackage.replace('.', '/');
            // 获取类加载器
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 获取包对应的资源
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                java.net.URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    // 如果是文件系统（开发环境）
                    scanDirectory(new File(resource.getFile()), basePackage);
                }
                // 还可以处理 jar 包中的类
            }
        } catch (Exception e) {
            throw new RuntimeException("Registration failed", e);
        }
    }

    private static void scanDirectory(File directory, String packageName) {
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // 处理 .class 文件
                String className = packageName + "."
                        + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                        RulesSetting annotation = clazz.getAnnotation(RulesSetting.class);
                        if (annotation!=null && annotation.enabled()) {
                            ruleClasses.add(clazz);
                            //System.out.println("Rule Class: " + clazz.getName());
                        }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
