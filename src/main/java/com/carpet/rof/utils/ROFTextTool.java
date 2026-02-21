package com.carpet.rof.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

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
    public static MutableText text(String string, UnaryOperator<Style>... styleUpdaters) {
        int len = string.length();
        int updaterIndex = 0;
        Deque<MutableText> textStack = new ArrayDeque<>();
        Deque<Style> styleStack = new ArrayDeque<>();
        textStack.push(Text.empty());
        styleStack.push(Style.EMPTY);
        int i = 0;
        while (i < len) {
            char ch = string.charAt(i);
            if (ch == '&') {
                if (i + 1 < len) {
                    char code = string.charAt(i + 1);
                    Style updated = styleStack.pop();
                    styleStack.push(updated.withFormatting(Formatting.byCode(code)));
                    i += 2;
                } else {
                    i++;
                }
                continue;
            }
            if (ch == '{') {
                textStack.push(Text.empty());
                styleStack.push(styleStack.peek());
                i++;
                continue;
            }
            if (ch == '}') {
                MutableText inner = textStack.pop();
                styleStack.pop();
                if (updaterIndex < styleUpdaters.length) {
                    UnaryOperator<Style> updater = styleUpdaters[updaterIndex++];
                    inner = inner.styled(updater);
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
                    Text.literal(string.substring(start, i)).styled(s -> segStyle));
        }
        return textStack.pop();
    }
    @SafeVarargs
    public static Supplier<Text> textS(String string, UnaryOperator<Style>... styleUpdaters) {
        return () -> text(string, styleUpdaters);
    }
    public static MutableText processDisplay(String taskName, double progress) {
        if (Double.isNaN(progress)) progress = 0.0;
        progress = Math.max(0.0, Math.min(1.0, progress));
        final int barWidth = 20;
        int filled = (int) Math.round(progress * barWidth);
        MutableText text = Text.empty();
        // 任务名
        text.append(Text.literal(taskName + " ")
                .formatted(progress >= 1.0 ? Formatting.GREEN : Formatting.GRAY));
        // 左括号
        text.append(Text.literal("[").formatted(Formatting.DARK_GRAY));
        // 已完成部分
        if (filled > 0) {
            text.append(Text.literal("▇".repeat(filled)).formatted(Formatting.GREEN));
        }
        // 未完成部分
        int empty = barWidth - filled;
        if (empty > 0) {
            text.append(Text.literal("▇".repeat(empty)).formatted(Formatting.DARK_GRAY));
        }
        // 右括号
        text.append(Text.literal("] ").formatted(Formatting.DARK_GRAY));
        // 百分比
        text.append(Text.literal(String.format("%5.1f%%", progress * 100)).formatted(Formatting.YELLOW));
        return text;
    }
}
