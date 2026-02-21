package com.carpet.rof.commands;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFCommandHelper;
import com.carpet.rof.utils.ROFTextTool;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.ROFTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static carpet.api.settings.RuleCategory.COMMAND;
import static com.carpet.rof.rules.BaseSetting.ROF;


@ROFCommand
@ROFRule
public class EntityTrackerCommand
{

    @Rule(
            categories = {COMMAND, ROF},
            strict = false,
            validators = {Validators.CommandLevel.class}
    )
    @QuickTranslations(
            name = "实体追踪",
            description = "可以选择一个实体进行追踪，其坐标与位置将会实时广播给玩家。"
    )
    public static String commandEntityTracker = "true";

    private static final Map<UUID, UUID> entityTrackerMap = new HashMap<>();

    static {
        ROFEvents.ServerStart.register(server -> entityTrackerMap.clear());

        ROFEvents.ServerTickEnd.register(minecraftServer -> {
            for (Map.Entry<UUID, UUID> entry : entityTrackerMap.entrySet()) {
                if (entry.getValue() == null) continue;
                var player = minecraftServer.getPlayerManager().getPlayer(entry.getKey());
                if (player == null) continue;

                Entity entity = getEntity(minecraftServer, entry.getValue());
                if (entity == null) continue;

                if (entity.isRemoved()) {
                    player.sendMessage(Text.literal("因为实体移除，实体追踪结束"), false);
                    entityTrackerMap.put(entry.getKey(), null);
                } else {
                    var text = ROFTextTool.text("Pos: &9" + ROFTool.toString_(ROFWarp.getPos_(entity))
                            + " &rVec: &9" + ROFTool.toString_(entity.getVelocity()));
                    player.sendMessage(text, true);
                }
            }
        });
    }

    @Nullable
    public static Entity getEntity(MinecraftServer server, UUID uuid) {
        for (var world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) return entity;
        }
        return null;
    }

    public static int addNormalEntity(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgumentType.getEntity(ctx, "entity");
        var player = ctx.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.literal("实体追踪开始，当前实体: ").append(entity.getName()));
            entityTrackerMap.put(player.getUuid(), entity.getUuid());
            return 0;
        }
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        ROFCommandHelper<ServerCommandSource> helper = new ROFCommandHelper<>(dispatcher.getRoot());



        helper.registerCommand("entityTracker{r} set <entity>")
                .rCarpet(()->commandEntityTracker)
                .arg(EntityArgumentType.entity())
                .command(EntityTrackerCommand::addNormalEntity);

        helper.registerCommand("entityTracker clear")
                .command(
                        ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player != null) {
                                entityTrackerMap.put(player.getUuid(), null);
                                player.sendMessage(Text.literal("实体追踪已清除"), false);
                                return 0;
                            }
                            return 1;
                        }
                );

    }
}
