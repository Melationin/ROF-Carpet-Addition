package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.debug.SprintTickTimer;
import com.carpet.rof.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;

@ROFCommand
@ROFRule
public class TickSprintFullCommand
{
    @Rule(categories = {ROF, COMMAND},
          strict = false,
          validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "加速逐tick耗时输出",
                       description = "为 /tick sprint 添加 full 参数，加速时输出每一个游戏刻的耗时",
                       extra = {"/tick sprint <time> full - 加速指定时间，并逐gt输出耗时",
                                "加速结束时输出耗时统计（平均 / p50 / p95 / p99 / 最大 / 等效tps）"}
                       )
    public static String commandTickSprintFull = "ops";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        CommandNode<CommandSourceStack> tick = dispatcher.getRoot().getChild("tick");
        if (tick == null) return;
        CommandNode<CommandSourceStack> sprint = tick.getChild("sprint");
        if (sprint == null) return;
        CommandNode<CommandSourceStack> time = sprint.getChild("time");
        if (time == null) return;
        new CommandHelper<>(time).registerCommand("full{r}")
                .rCarpet(() -> commandTickSprintFull)
                .command(context -> sprintFull(context.getSource(), IntegerArgumentType.getInteger(context, "time")));
    }

    private static int sprintFull(CommandSourceStack source, int time)
    {
        boolean interrupted = source.getServer().tickRateManager().requestGameToSprint(time);
        SprintTickTimer.start(source);
        if (interrupted)
        {
            source.sendSuccess(() -> Component.translatable("commands.tick.sprint.stop.success"), true);
        }

        source.sendSuccess(() -> Component.translatable("commands.tick.status.sprinting"), true);
        return 1;
    }
}
