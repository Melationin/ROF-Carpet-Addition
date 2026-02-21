package com.carpet.rof.commands.loggerCommand;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.logger.packetLogger.PacketLogger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Map;

import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static net.minecraft.server.command.CommandManager.literal;

@ROFRule
@ROFCommand
public class PacketLoggerCommand
{


    @Rule(
            categories = {COMMAND,ROF}, strict = false, validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "数据包监视器Plus",
            description = "可以记录各种数据包的发包数量与压缩前大小。同时，也为发包限制的前置。")
    public static String commandPacketLoggerPlus = "ops";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("packetLogger").requires(
                        source -> carpet.utils.CommandHelper.canUseCommand(source, commandPacketLoggerPlus))
                .then(literal("start").executes(ctx ->
                {

                    if (PacketLogger.instance == null) {
                        PacketLogger.instance = new PacketLogger();
                    }
                    PacketLogger.instance.start(ctx.getSource().getWorld().getTime());

                    ctx.getSource().sendFeedback(() -> Text.of("数据包记录器已启用"), false);
                    return 0;
                })).then(literal("stop").executes(ctx ->
                {

                    if (PacketLogger.instance == null) {
                        ctx.getSource().sendFeedback(() -> Text.of("数据包记录器未启用！"), false);
                    }
                    PacketLogger.instance.stop(ctx.getSource().getWorld().getTime());
                    return printPacketData(ctx);
                })).executes(ctx ->
                {
                    if (PacketLogger.instance == null) {
                        ctx.getSource().sendFeedback(() -> Text.of("数据包记录器未启用！"), false);
                        return 0;
                    }
                    PacketLogger.instance.setEndtime(ctx.getSource().getWorld().getTime());
                    return printPacketData(ctx);
                }));
    }

    private static int printPacketData(CommandContext<ServerCommandSource> ctx)
    {
        ArrayList<java.util.Map.Entry<net.minecraft.network.packet.PacketType<?>, Long>> list = new ArrayList<>(
                PacketLogger.instance.packetSizeMap.entrySet());

        list.sort(java.util.Map.Entry.<PacketType<?>, Long>comparingByValue().reversed());

        long ticks = PacketLogger.instance.getTicks();
        long realtime = PacketLogger.instance.getRealTime();

        long allSize = list.stream().mapToLong(Map.Entry::getValue).sum();


        ctx.getSource().sendFeedback(() -> Text.of(
                        "过去" + ticks + " tick / " + realtime / 1000.0 + "s 内, 发包总量为：" + byteSizeToString(allSize)),
                false);
        for (var item : list) {
            ctx.getSource()
                    .sendFeedback(() -> Text.of(item.getKey().id() + " : " + byteSizeToString(item.getValue())), false);
        }
        return 0;
    }

    private static String byteSizeToString(long size)
    {
        if (size <= 4 * 1024)
            return size + "B";
        if (size <= 8 * 1024 * 1024)
            return size / 1024 + "KB";
        return size / (1024 * 1024) + "MB";
    }
}
