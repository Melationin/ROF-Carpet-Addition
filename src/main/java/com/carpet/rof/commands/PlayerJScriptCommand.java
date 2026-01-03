package com.carpet.rof.commands;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.CarpetCommand;
import com.carpet.rof.rules.playerJScript.ServerPlayerEntityAccessor;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


import javax.swing.text.html.parser.Entity;

import java.util.function.Supplier;

import static com.carpet.rof.rules.playerJScript.PlayerJScriptSetting.commandPlayerJScript;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


@CarpetCommand
public class PlayerJScriptCommand {
    public static void registerDispatcher(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("playerJScript")
                .requires(source -> carpet.utils.CommandHelper.canUseCommand(source, commandPlayerJScript))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("start").executes((context)->{
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                            if(player instanceof EntityPlayerMPFake playerMPFake){
                                ((ServerPlayerEntityAccessor)playerMPFake).setPlayerJScriptManager(null);
                                var script = ((ServerPlayerEntityAccessor)playerMPFake).getPlayerJScriptManager();
                                script.start();
                                return 0;
                            }else {
                                context.getSource().sendFeedback(()->{
                                   return  Text.of("必须为假人");
                                },false);
                                return 0;
                            }
                        }))
                        .then(CommandManager.literal("stop").executes((context)->{
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                            if(player instanceof EntityPlayerMPFake playerMPFake){
                                ((ServerPlayerEntityAccessor)playerMPFake).setPlayerJScriptManager(null);
                                var script = ((ServerPlayerEntityAccessor)playerMPFake).getPlayerJScriptManager();
                                script.stop();
                                return 0;
                            }else {
                                context.getSource().sendFeedback(()->{
                                    return  Text.of("必须为假人");
                                },false);
                                return 0;
                            }
                        }))
        ));
    }
}
