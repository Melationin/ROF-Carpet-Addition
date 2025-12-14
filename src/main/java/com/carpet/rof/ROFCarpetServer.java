package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;


import com.carpet.rof.utils.RofCarpetTranslations;
import com.carpet.rof.utils.HighChunkSet;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//? >= 1.21.11 {
/*import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.command.permission.Permissions;

*///?}
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

//import static com.carpet.rof.utils.PacketRecord.packetRecord;
import static net.minecraft.server.command.CommandManager.*;

import java.util.Map;

public class ROFCarpetServer implements CarpetExtension, ModInitializer
{
    @Override
    public String version()
    {
        return "carpet-YCT";
    }

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

    public boolean hasPermission(ServerCommandSource source)  {
        //? >= 1.21.11 {
        /*return source.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(4)));
        *///?} else {
            return source.hasPermissionLevel(4);

        //?}

    }

    @Override
    public void onGameStarted()
    {
        CarpetServer.settingsManager.parseSettingsClass(ROFCarpetSettings.class);

       CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
               {
                dispatcher.register(literal("highChunkListener")
                                .requires(this::hasPermission /*hasPermissionLevel(4)*/ )
                                .requires((source) -> carpet.utils.CommandHelper.canUseCommand(source, ROFCarpetSettings.highChunkListener))
                                .then(literal("loadFromWorld").executes(context -> {
                            Thread tempThread = new HighChunkSet.ReloadThread(NETHER_HighChunkSet,
                                    context
                            );
                            tempThread.start();

                            return 1;
                        }))
                                .then(literal("save").executes(context -> {
                                    NETHER_HighChunkSet.Save();
                                    context.getSource().sendFeedback(()->Text.of("Save " + NETHER_HighChunkSet.size()+" Chunks"),true);
                                    return 1;
                                }))
                                .then(literal("loadFromFile").executes(context ->{
                                    if(context.getSource().getPlayer() instanceof PlayerEntity player)
                                  if( NETHER_HighChunkSet.load()){
                                      player.sendMessage(Text.of("Finished Loading highChunkSet.dat "),false);
                                      player.sendMessage(Text.of(NETHER_HighChunkSet.size() + " highChunks loaded"),false);
                                  }else {
                                      player.sendMessage(Text.of("Failed Loading highChunkSet.dat "),false);
                                  }return  1;
                                }))
                                .then(literal("clear").executes(commandContext -> {
                                    NETHER_HighChunkSet.clear();
                                    commandContext.getSource().sendFeedback(()->Text.of("Clear HighChunkSet Finished "),true);
                                    return 1;
                                        }
                                ))
                        );
               }

       );


    }


    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }
}
