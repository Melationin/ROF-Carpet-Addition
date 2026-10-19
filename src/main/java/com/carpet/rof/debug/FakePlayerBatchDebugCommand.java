package com.carpet.rof.debug;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

@ROFCommand
public final class FakePlayerBatchDebugCommand
{
    private FakePlayerBatchDebugCommand()
    {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug fakePlayers <prefix> <count>")
                .arg(StringArgumentType.word())
                .arg(IntegerArgumentType.integer(1, 512))
                .command(FakePlayerBatchDebugCommand::spawn);
        helper.registerCommand("rofDebug killFakePlayers <prefix>")
                .arg(StringArgumentType.word())
                .command(FakePlayerBatchDebugCommand::kill);
    }

    private static int spawn(CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        String prefix = StringArgumentType.getString(context, "prefix");
        int count = IntegerArgumentType.getInteger(context, "count");
        int queued = 0;
        for (int i = 1; i <= count; i++)
        {
            if (dispatch(source, "player " + prefix + i + " spawn") > 0)
            {
                queued++;
            }
        }
        int result = queued;
        source.sendSuccess(() -> Component.literal("ROF fake players queued: " + result + "/" + count), false);
        return result;
    }

    private static int kill(CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        String prefix = StringArgumentType.getString(context, "prefix");
        int removed = 0;
        for (ServerPlayer player : List.copyOf(source.getServer().getPlayerList().getPlayers()))
        {
            if (!(player instanceof EntityPlayerMPFake))
            {
                continue;
            }
            String name = ROFWarp.getProfileName(player);
            if (!name.startsWith(prefix))
            {
                continue;
            }
            dispatch(source, "player " + name + " kill");
            removed++;
        }
        int result = removed;
        source.sendSuccess(() -> Component.literal("ROF fake players killed: " + result), false);
        return result;
    }

    // 走命令分发器，等价于在主线程手动输入 /player ...，因此可复用 carpet 的全部校验与提示。
    private static int dispatch(final CommandSourceStack source, final String command)
    {
        try
        {
            return source.getServer().getCommands().getDispatcher().execute(command, source);
        }
        catch (CommandSyntaxException e)
        {
            ROFTool.rDEBUG("command rejected: " + command + " -> " + e.getMessage());
            return 0;
        }
        catch (RuntimeException e)
        {
            ROFTool.rDEBUG("command failed: " + command + " -> " + e);
            return 0;
        }
    }
}
