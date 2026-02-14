package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;

import com.carpet.rof.utils.RofCarpetTranslations;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;


//import static com.carpet.rof.utils.PacketRecord.packetRecord;

import java.util.Map;

public class ROFCarpetServer implements CarpetExtension, ModInitializer
{

    // public static ROFCarpetServer instance;

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
        //RuleClasses.add(ROFCarpetSettings.class);
        ROFSettings.loadClasses();
        for (var r : ROFSettings.ruleClasses) {
            CarpetServer.settingsManager.parseSettingsClass(r);
        }
    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext)
    {
        ROFCommands.register(dispatcher);
    }


    @Override
    public void registerLoggers(){
        ROFLoggers.register();
    }
}
