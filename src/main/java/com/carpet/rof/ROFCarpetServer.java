package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;



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

//import static com.carpet.rof.utils.PacketRecord.packetRecord;

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
        ROFCarpetServer.loadExtension();
    }



    @Override
    public void onGameStarted()
    {

        //RuleClasses.add(ROFCarpetSettings.class);
        Settings.loadClasses();

        for(var r : Settings.ruleClasses){
            CarpetServer.settingsManager.parseSettingsClass(r);
        }


    }


    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        System.out.println("onHasTranslations: " + lang);
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }


    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        RofCommands.register(dispatcher);
    }
}
