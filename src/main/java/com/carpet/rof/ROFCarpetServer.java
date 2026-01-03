package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;


import com.carpet.rof.annotation.RulesSetting;
import com.carpet.rof.js.sandbox.SafeRhino;
import com.carpet.rof.rules.highChunkListener.HighChunkSet;

import com.carpet.rof.utils.RofCarpetTranslations;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
//? >= 1.21.11 {
/*import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.command.permission.Permissions;

*///?}

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class ROFCarpetServer implements CarpetExtension, ModInitializer
{
    @Override
    public String version()
    {
        return "ROF";
    }

   // public static ROFCarpetServer instance;

    public static void loadExtension()
    {
        CarpetServer.manageExtension(new ROFCarpetServer());
    }

    public static HighChunkSet NETHER_HighChunkSet;


    @Override
    public void onInitialize()
    {

        SafeRhino safeRhino = new SafeRhino();

        Object result = safeRhino.execute("""
            // 安全代码示例
            console.log('Hello Minecraft!');
            
            var x = 10;
            var y = 20;
            var sum = x + y;
            
            // 简单函数
            function calculate(a, b) {
                return a * b + sum;
            }
            
            // 使用console对象
            console.log('计算结果:', calculate(5, 3));
            
            // 返回结果
            '执行成功';
            """);
        // 3. 处理结果
        if (result != null) {
            System.out.println("执行成功，结果: " + result);
        } else {
            System.out.println("执行被阻止或失败");
        }

        ROFCarpetServer.loadExtension();
    }



    @Override
    public void onGameStarted()
    {

       ROFSettings.register();

        for(var r : ROFSettings.ruleClasses){
            CarpetServer.settingsManager.parseSettingsClass(r);
        }
        //CarpetServer.settingsManager.parseSettingsClass(ROFCarpetSettings.class);

        SafeRhino.INSTANCE = new SafeRhino();

    }


    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        ROFCommand.register(dispatcher, commandBuildContext);
    }
}
