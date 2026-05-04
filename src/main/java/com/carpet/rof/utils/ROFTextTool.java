package com.carpet.rof.utils;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ROFTextTool
{
    private static final String PREFIX_OVERWORLD = "overworld";
    private static final String PREFIX_NETHER    = "the_nether";
    private static final String PREFIX_END       = "the_end";

    public static String getWorldName(String worldName) {
        if (worldName.contains(PREFIX_OVERWORLD)) return "&r&2&lOVERWORLD";
        if (worldName.contains(PREFIX_NETHER))    return "&r&4&lTHE NETHER";
        if (worldName.contains(PREFIX_END))       return "&r&5&lTHE END";
        return "&r&l" + worldName.toUpperCase();
    }

    public static String getStringToClip(BlockPos pos) {
        return String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * text: 快速的生成一个Text
     *
     * @param string        被解析的字符串。&x为表示更改格式，{}表示应用styleUpdaters 参数。应用顺序以}为准。
     * @param styleUpdaters 格式
     * @return 生成的文本
     */
    @SafeVarargs
    public static MutableComponent text(String string, UnaryOperator<Style>... styleUpdaters) {
        int len = string.length();
        int updaterIndex = 0;
        Deque<MutableComponent> textStack = new ArrayDeque<>();
        Deque<Style> styleStack = new ArrayDeque<>();
        textStack.push(Component.empty());
        styleStack.push(Style.EMPTY);
        int i = 0;
        while (i < len) {
            char ch = string.charAt(i);
            if (ch == '&') {
                if (i + 1 < len) {
                    char code = string.charAt(i + 1);
                    Style updated = styleStack.pop();
                    styleStack.push(updated.applyFormat(ChatFormatting.getByCode(code)));
                    i += 2;
                } else {
                    i++;
                }
                continue;
            }
            if (ch == '{') {
                textStack.push(Component.empty());
                styleStack.push(styleStack.peek());
                i++;
                continue;
            }
            if (ch == '}') {
                MutableComponent inner = textStack.pop();
                styleStack.pop();
                if (updaterIndex < styleUpdaters.length) {
                    UnaryOperator<Style> updater = styleUpdaters[updaterIndex++];
                    inner = inner.withStyle(updater);
                }
                Objects.requireNonNull(textStack.peek()).append(inner);
                i++;
                continue;
            }
            // Accumulate a run of plain characters sharing the same style
            final Style segStyle = styleStack.peek();
            int start = i;
            while (i < len && string.charAt(i) != '&' && string.charAt(i) != '{' && string.charAt(i) != '}') {
                i++;
            }
            Objects.requireNonNull(textStack.peek()).append(
                    Component.literal(string.substring(start, i)).withStyle(s -> segStyle));
        }
        return textStack.pop();
    }
    @SafeVarargs
    public static Supplier<Component> textS(String string, UnaryOperator<Style>... styleUpdaters) {
        return () -> text(string, styleUpdaters);
    }
    public static MutableComponent processDisplay(String taskName, double progress) {
        if (Double.isNaN(progress)) progress = 0.0;
        progress = Math.max(0.0, Math.min(1.0, progress));
        final int barWidth = 20;
        int filled = (int) Math.round(progress * barWidth);
        MutableComponent text = Component.empty();
        // 任务名
        text.append(Component.literal(taskName + " ")
                .withStyle(progress >= 1.0 ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        // 左括号
        text.append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY));
        // 已完成部分
        if (filled > 0) {
            text.append(Component.literal("▇".repeat(filled)).withStyle(ChatFormatting.GREEN));
        }
        // 未完成部分
        int empty = barWidth - filled;
        if (empty > 0) {
            text.append(Component.literal("▇".repeat(empty)).withStyle(ChatFormatting.DARK_GRAY));
        }
        // 右括号
        text.append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY));
        // 百分比
        text.append(Component.literal(String.format("%5.1f%%", progress * 100)).withStyle(ChatFormatting.YELLOW));
        return text;
    }
}
