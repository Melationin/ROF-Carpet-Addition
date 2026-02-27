package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.carpet.rof.commands.RequirementModifyCommand;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFConfig;
import com.carpet.rof.utils.RofCarpetTranslations;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
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
        ROFSettings.loadClasses();
        for (Class<?> r : ROFSettings.ruleClasses) {
            CarpetServer.settingsManager.parseSettingsClass(r);
        }
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        SingleTaskWorker.INSTANCE.stop();
    }
    @Override
    public void onServerLoaded(MinecraftServer server)
    {
        initOnServer(server);
        ROFEvents.ServerStart.run(server);
    }

    public void initOnServer(MinecraftServer server){
        SingleTaskWorker.INSTANCE.start();
        ROFConfig.INSTANCE = new ROFConfig(server.getSavePath(WorldSavePath.ROOT).resolve("carpet-rof-addition.json"));
        ROFConfig.INSTANCE.load();
        RequirementModifyCommand.initialization(server,ROFConfig.INSTANCE);
        ROFEvents.ServerSave.register(server2 -> {
            ROFConfig.INSTANCE.set("requirementModifyMap", requirementModifyMap);
            ROFConfig.INSTANCE.save();
        });
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
