package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.RofTool;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static com.carpet.rof.utils.RofTool.text;
import static com.carpet.rof.utils.RofTool.textS;

@ROFRule
@ROFCommand
public class BetterPlayerCommand
{

    @Rule(
            categories = {ROF,COMMAND,CREATIVE},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "真人召唤命令",
            description = "添加真人召唤命令",
            extra = {
                    "/playerReal <realPlayer> spawn"
            }
    )
    public static String commandSpawnWhitedListedPlayer = "ops";


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {

        final SuggestionProvider<ServerCommandSource> REAL_PLAYER_SUGGEST = (context, builder) ->
        {
            var whiteList =  context.getSource().getServer().getPlayerManager().getWhitelistedNames();

            for(String player : whiteList){
                builder.suggest(player);
            }
            return builder.buildFuture();
        };


        Command<ServerCommandSource> command = (context)->{

            String playerName = StringArgumentType.getString(context,"realPlayer");
            if(context.getSource().getPlayerNames().contains(playerName)){
                context.getSource().sendFeedback(textS("&c玩家已存在!"), false);
                return 0;
            }

            var whiteList =  context.getSource().getServer().getPlayerManager().getWhitelist();

            if(!List.of(whiteList.getNames()).contains(playerName)){
                context.getSource().sendFeedback(textS("&cNot in WhiteList!"), false);
                return 0;
            }

            if(context.getSource().getPlayer() instanceof  ServerPlayerEntity player){
                boolean flying = false;
                var mode = ROFWarp.getGameMode(player);
                if (mode == GameMode.SPECTATOR) {
                    flying = true;
                } else if (mode.isSurvivalLike()) {
                    flying = false;
                }
                EntityPlayerMPFake.createFake(playerName, context.getSource().getServer(),
                        RofTool.getPos_(player),
                        player.getYaw(),
                        player.getPitch(),
                        RofTool.getWorld_(player).getRegistryKey(),
                        mode ,
                        flying);
            }
            return 1;
        };


        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("playerReal{r} <realPlayer>{s} spawn",command
        ,       ROFCommandHelper.carpetRequire(commandSpawnWhitedListedPlayer ),
                StringArgumentType.word(),
                REAL_PLAYER_SUGGEST
        );
    }
}
