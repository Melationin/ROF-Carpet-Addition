package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;


import com.carpet.rof.utils.RofCarpetTranslations;
import com.carpet.rof.utils.HighChunkSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

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

    @Override
    public void onGameStarted()
    {
        CarpetServer.settingsManager.parseSettingsClass(ROFCarpetSettings.class);

       CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->

                dispatcher.register(literal("highChunkListener")
                                .requires(source -> source.hasPermissionLevel(4))
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
                        /*
                                .then(literal("setDimension").executes(commandContext -> {
                                            NETHER_HighChunkSet.world = commandContext.getSource().getWorld();
                                    commandContext.getSource().sendFeedback(()->Text.of("已设置维度为"+NETHER_HighChunkSet.world .getDimensionEntry().getIdAsString()),true);
                                            return 1;
                                        }))
                                .then(literal("setTopY")
                                        .then(argument("TopY", IntegerArgumentType.integer())
                                        .executes(commandContext -> {
                                            int TopY = IntegerArgumentType.getInteger(commandContext, "TopY");
                                            if(TopY < NETHER_HighChunkSet.world.getBottomY() || TopY > NETHER_HighChunkSet.world.getBottomY()+NETHER_HighChunkSet.world.getHeight()) {
                                                commandContext.getSource().sendFeedback(()->Text.of("请输入正确的范围"),false);

                                                return 0;
                                            }
                                            NETHER_HighChunkSet.topY=TopY;
                                            commandContext.getSource().sendFeedback(()-> Text.of("已设置高度为："+TopY),true);

                                            return 1;
                                        })
                                ))

                         */
                                .then(literal("clear").executes(commandContext -> {
                                    NETHER_HighChunkSet.clear();
                                    commandContext.getSource().sendFeedback(()->Text.of("Clear HighChunkSet Finished "),true);
                                    return 1;
                                        }

                                ))
                        ));


    }


    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return RofCarpetTranslations.getTranslationFromResourcePath(lang);
    }
}
