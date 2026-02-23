package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import static carpet.api.settings.RuleCategory.*;
import static com.carpet.rof.rules.BaseSetting.ROF;
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
            name = "无前缀假人召唤命令",
            description = "用于召唤无前缀假人",
            extra = {
                    "/player <name> spawn original - 召唤一个无前缀假人"
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
            String playerName = StringArgumentType.getString(context,"player");

            if (context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
                var mode = ROFWarp.getGameMode(player);
                boolean flying = !mode.isSurvivalLike();
                EntityPlayerMPFake.createFake(playerName, context.getSource().getServer(),
                        ROFWarp.getPos_(player),
                        player.getYaw(),
                        player.getPitch(),
                        ROFWarp.getWorld_(player).getRegistryKey(),
                        mode,
                        flying);
            }
            return 1;
        };


        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("player <player>{s} spawn original{r}")
                .arg(StringArgumentType.word())
                .s(REAL_PLAYER_SUGGEST)
                .rCarpet(()->commandSpawnWhitedListedPlayer)
                .command(command);
    }
}
