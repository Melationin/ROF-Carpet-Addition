package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;



import com.carpet.rof.rules.highChunkListener.HighChunkSet;

import com.carpet.rof.utils.RofCarpetTranslations;
import com.carpet.rof.utils.RofTool;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//? >= 1.21.11 {
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.command.permission.Permissions;

//?}
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

//import static com.carpet.rof.utils.PacketRecord.packetRecord;
import static com.carpet.rof.rules.autoFreeze.AutoFreezeSettings.unfreezeCommand;
import static com.carpet.rof.rules.highChunkListener.HighChunkListenerSetting.highChunkListener;
import static net.minecraft.server.command.CommandManager.*;

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
        ROFCarpetServer.loadExtension();
    }



    @Override
    public void onGameStarted()
    {

        //RuleClasses.add(ROFCarpetSettings.class);
        Settings.scanAndRegister("com.carpet.rof.rules");

        for(var r : Settings.ruleClasses){
            CarpetServer.settingsManager.parseSettingsClass(r);
        }
        //CarpetServer.settingsManager.parseSettingsClass(ROFCarpetSettings.class);

       RofCommand.register();
    }


    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }

}
