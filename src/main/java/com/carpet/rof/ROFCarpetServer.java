package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFConfig;
import com.carpet.rof.utils.RofCarpetTranslations;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.util.HashMap;
import java.util.Map;

import static com.carpet.rof.commands.RequirementModifyCommand.requirementModifyMap;
import static com.carpet.rof.utils.ROFTool.getSavePath;

public class ROFCarpetServer implements CarpetExtension, ModInitializer
{
    public static void loadExtension()
    {

        CarpetServer.manageExtension(new ROFCarpetServer());
        CarpetServer.registerExtensionLoggers();
    }

    @Override
    public String version()
    {
        return "ROF";
    }

    @Override
    public void onInitialize()
    {
        ROFCarpetServer.loadExtension();
    }

    @Override
    public void onGameStarted()
    {
        if(ROFConfig.INSTANCE == null){};
        ROFSettings.loadClasses();
        for (Class<?> r : ROFSettings.ruleClasses) {
            CarpetServer.settingsManager.parseSettingsClass(r);
        }



    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath("zh_cn");
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext)
    {
        ROFCommands.register(dispatcher);
    }

    @Override
    public void registerLoggers()
    {
        ROFLoggers.register();
    }
}
