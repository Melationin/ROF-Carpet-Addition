package com.carpet.rof.commands;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.utils.CommandHelper;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.ROFWarp;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@ROFCommand
public final class FakePlayerMatrixDebugCommand {
    private FakePlayerMatrixDebugCommand() {}

    private static final String FAKE_PREFIX = "rof_fp_";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!ROFTool.DEBUG) return;
        CommandHelper<CommandSourceStack> helper = new CommandHelper<>(dispatcher.getRoot());
        helper.registerCommand("rofDebug fakePlayerMatrix remove").command(context -> {
            int removed = killFakes(context.getSource(), true);
            context.getSource().sendSuccess(() -> Component.literal("ROF fake players removed: " + removed), false);
            return removed;
        });
        helper.registerCommand("rofDebug fakePlayerMatrix <c>")
                .arg(DoubleArgumentType.doubleArg(0.0))
                .command(context -> spawnMatrix(context.getSource(), DoubleArgumentType.getDouble(context, "c")));
    }

    private static int spawnMatrix(CommandSourceStack source, double c) {
        if (!(source.getPlayer() instanceof ServerPlayer player)) return 0;
        killFakes(source, false);
        var level = ROFWarp.getWorld_(player);
        var mode = ROFWarp.getGameMode(player);
        boolean flying = !mode.isSurvival();
        Vec3 center = ROFWarp.getPos_(player);
        int spawned = 0;
        for (int i = 0; i < 25; i++) {
            Vec3 pos = center.add(((i % 5) - 2) * c, 0.0, ((i / 5) - 2) * c);
            if (EntityPlayerMPFake.createFake(FAKE_PREFIX + i, source.getServer(), pos,
                    player.getYRot(), player.getXRot(), level.dimension(), mode, flying)) {
                spawned++;
            }
        }
        int result = spawned;
        source.sendSuccess(() -> Component.literal("ROF fake player matrix: " + result + "/25"), false);
        return result;
    }

    private static int killFakes(CommandSourceStack source, boolean all) {
        int removed = 0;
        for (ServerPlayer player : List.copyOf(source.getServer().getPlayerList().getPlayers())) {
            if (player instanceof EntityPlayerMPFake fake
                    && (all || fake.getGameProfile().name().startsWith(FAKE_PREFIX))) {
                fake.kill(fake.level());
                removed++;
            }
        }
        return removed;
    }
}
