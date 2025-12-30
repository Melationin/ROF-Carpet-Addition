package com.carpet.rof;

import com.carpet.rof.rules.highChunkListener.HighChunkSet;
import com.carpet.rof.utils.RofTool;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.carpet.rof.ROFCarpetServer.NETHER_HighChunkSet;
import static com.carpet.rof.rules.autoFreeze.AutoFreezeSettings.unfreezeCommand;
import static com.carpet.rof.rules.highChunkListener.HighChunkListenerSetting.highChunkListener;
import static net.minecraft.server.command.CommandManager.literal;

public class RofCommand {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                {
                    dispatcher.register(literal("highChunkListener")
                            .requires(RofTool::hasPermission  )
                            .requires((source) -> carpet.utils.CommandHelper.canUseCommand(source, highChunkListener))
                            .then(literal("loadFromWorld").executes(context -> {
                                Thread tempThread = new HighChunkSet.ReloadThread(NETHER_HighChunkSet,
                                        context
                                );
                                tempThread.start();

                                return 1;
                            }))
                            .then(literal("save").executes(context -> {
                                NETHER_HighChunkSet.Save();
                                context.getSource().sendFeedback(()-> Text.of("Save " + NETHER_HighChunkSet.size()+" Chunks"),true);
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

                    dispatcher.register(literal("unfreeze")
                            .requires(source -> unfreezeCommand)
                            .executes(context->{
                                context.getSource().getServer().getTickManager().setFrozen(false);
                                return 0;
                            }));
                }

        );
    }
}
