package com.carpet.rof.commands.loggerCommand;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;
import com.carpet.rof.annotation.QuickTranslations;
import com.carpet.rof.annotation.ROFCommand;
import com.carpet.rof.annotation.ROFRule;
import com.carpet.rof.logger.packetLogger.PacketLogger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Map;

import static carpet.api.settings.RuleCategory.COMMAND;
import static carpet.api.settings.RuleCategory.EXPERIMENTAL;
import static com.carpet.rof.rules.BaseSetting.ROF;
import static net.minecraft.commands.Commands.literal;

@ROFRule
@ROFCommand
public class PacketLoggerCommand
{


    @Rule(
            categories = {COMMAND,ROF}, strict = false, validators = {Validators.CommandLevel.class})
    @QuickTranslations(name = "数据包监视器Plus",
            description = "记录各种数据包的压缩前大小。",
                       extra = {"/packetLogger start - 开始记录数据包",
                               "/packetLogger stop - 结束记录并显示数据",
                               "/packetLogger - 显示当前数据（如果正在记录）"
                                }
    )
    public static String commandPacketLoggerPlus = "ops";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(literal("packetLogger").requires(
                        source -> carpet.utils.CommandHelper.canUseCommand(source, commandPacketLoggerPlus))
                .then(literal("start").executes(ctx ->
                {

                    if (PacketLogger.instance == null) {
                        PacketLogger.instance = new PacketLogger();
                    }
                    PacketLogger.instance.start(ctx.getSource().getLevel().getGameTime());

                    ctx.getSource().sendSuccess(() -> Component.nullToEmpty("Packet logger enabled"), false);
                    return 0;
                })).then(literal("stop").executes(ctx ->
                {

                    if (PacketLogger.instance == null) {
                        ctx.getSource().sendFailure(Component.nullToEmpty("Packet logger not enabled!"));
                    }
                    PacketLogger.instance.stop(ctx.getSource().getLevel().getGameTime());
                    return printPacketData(ctx);
                })).executes(ctx ->
                {
                    if (PacketLogger.instance == null) {
                        ctx.getSource().sendFailure(Component.nullToEmpty("Packet logger not enabled!"));
                        return 0;
                    }
                    PacketLogger.instance.setEndtime(ctx.getSource().getLevel().getGameTime());
                    return printPacketData(ctx);
                }));
    }

    private static int printPacketData(CommandContext<CommandSourceStack> ctx)
    {
        ArrayList<java.util.Map.Entry<net.minecraft.network.protocol.PacketType<?>, Long>> list = new ArrayList<>(
                PacketLogger.instance.packetSizeMap.entrySet());

        list.sort(java.util.Map.Entry.<PacketType<?>, Long>comparingByValue().reversed());

        long ticks = PacketLogger.instance.getTicks();
        long realtime = PacketLogger.instance.getRealTime();

        long allSize = list.stream().mapToLong(Map.Entry::getValue).sum();


        ctx.getSource().sendSuccess(() -> Component.nullToEmpty(
                        "In the past " + ticks + " tick(s) / " + realtime / 1000.0 + "s, total packet size: " + byteSizeToString(allSize)),
                false);
        for (var item : list) {
            ctx.getSource()
                    .sendSuccess(() -> Component.nullToEmpty(item.getKey().id() + " : " + byteSizeToString(item.getValue())), false);
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
