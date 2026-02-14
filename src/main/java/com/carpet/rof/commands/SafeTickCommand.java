package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@ROFRule
@ROFCommand
public class SafeTickCommand
{

    public static int SafeTickRate = -1; //-1 表示不开启

    @Rule(categories = {ROF, COMMAND}, strict = false, validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "安全加速", description = "添加了tick的子命令safe rate , 可以在无生存真人时自动加速")
    public static String commandSafeTick = "false";


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        CommandNode<ServerCommandSource> root = dispatcher.getRoot();
        CommandNode<ServerCommandSource> carpetNode = root.getChild("tick");

        var n1 = literal("safe").requires((source) ->
        {
            return carpet.utils.CommandHelper.canUseCommand(source, commandSafeTick);
        });
        n1 = n1.then(literal("rate").then(argument("rate", IntegerArgumentType.integer()).executes((context ->
        {
            SafeTickRate = IntegerArgumentType.getInteger(context, "rate");
            if (SafeTickRate == 20)
                context.getSource().sendFeedback(() -> Text.of("Set safe tick rate to " + SafeTickRate), true);
            return 0;
        }))));
        LiteralArgumentBuilder<ServerCommandSource> mineCmd = n1;
        carpetNode.addChild(mineCmd.build());
    }
}
