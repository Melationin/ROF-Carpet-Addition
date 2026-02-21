package com.carpet.rof.commands;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFRule
@ROFCommand
public class SafeTickCommand
{


    public static int SafeTickRate = -1; //-1 表示不开启

    @Rule(categories = {ROF, COMMAND}, strict = false)
    @QuickTranslations(name = "安全加速", description = "添加了命令tickSafe , 可以在无生存真人时自动加速")
    public static String commandSafeTick = "false";


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("tickSafe{r} rate <rate2>")
                //.r(source -> carpet.utils.CommandHelper.canUseCommand((ServerCommandSource) source, commandSafeTick)) //没问题！
                 .rCarpet(()->commandSafeTick) //有问题！！
                .arg(IntegerArgumentType.integer(0))
                .command(context->{
                    SafeTickRate = IntegerArgumentType.getInteger(context, "rate2");
                    if (SafeTickRate != 20)
                        context.getSource().sendFeedback(() -> Text.of("Set safe tick rate to " + SafeTickRate), true);
                    return 0;
                });
    }

    static {
        ROFEvents.ServerStart.register(server -> {
            SafeTickCommand.SafeTickRate = -1;
        });
        ROFEvents.ServerTickBegin.register(server -> {
            var playerList = server.getPlayerManager().getPlayerList();
            int survivalRealPlayerCount = 0;
            for (var player : playerList) {
                if (!player.isSpectator() && !(player instanceof EntityPlayerMPFake)) {
                    survivalRealPlayerCount++;
                }
            }

            if (survivalRealPlayerCount == 0) {
                if (server.getTickManager().shouldTick()) {
                    if (!server.getTickManager().isSprinting() && false) {
                        server.getTickManager().startSprint(20 * 60 * 60 * 365);
                        server.sendMessage(Text.of("Start tick sprint!!!"));
                    } else if (SafeTickCommand.SafeTickRate >= 0 && server.getTickManager()
                            .getTickRate() != SafeTickCommand.SafeTickRate) {
                        server.getTickManager().setTickRate(SafeTickCommand.SafeTickRate);
                    }
                }
            } else {
                if (SafeTickCommand.SafeTickRate != -1) {
                    if (server.getTickManager().shouldTick()) {
                        if (server.getTickManager().isSprinting()) {
                            server.getTickManager().stopSprinting();
                            server.getCommandSource().sendFeedback(() -> Text.of("Stop tick sprint!!!"), true);
                        }
                        if (server.getTickManager().getTickRate() != 20) {
                            server.getTickManager().setTickRate(20);
                            server.getCommandSource().sendFeedback(() -> Text.of("Set tick rate to 20"), true);
                        }
                    }
                }
            }
        });
    }
}
