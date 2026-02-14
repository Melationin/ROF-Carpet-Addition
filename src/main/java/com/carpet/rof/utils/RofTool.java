package com.carpet.rof.utils;

import carpet.script.external.Vanilla;


import com.mojang.logging.LogUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static net.minecraft.world.dimension.DimensionTypes.THE_NETHER;

public class RofTool {
    public static Boolean isNetherWorld(World world){
        return world.getDimensionEntry().matchesKey(THE_NETHER);
    }

    public static Path getSavePath(ServerWorld world) {
        return Vanilla.MinecraftServer_storageSource(world.getServer()).getWorldDirectory(world.getRegistryKey());
    }




    public static int fastHash(int x){
        x = ((x >> 8) ^ x) * 0x119de1f3;
        x = ((x >> 8) ^ x) * 0x119de1f3;
        x = (x >> 8) ^ x;
        return x & 0xFFFF;
    }


    public static ChunkPos getChunkPos(long value)
    {
        return new ChunkPos((int) (value & 0xFFFFFFFFL), (int) ((value >> 32) & 0xFFFFFFFFL));
    }

    public static World getWorld_(Entity entity) {
        //? if >=1.21.10 {
        /*return entity.getEntityWorld();
        *///?} else {
        return entity.getWorld();
        //?}
    }

    public static Vec3d getPos_(Entity entity) {
        //? if >=1.21.10 {
            /*return entity.getEntityPos();

        *///?} else {
        return entity.getPos();
        //?}
    }

    public static String toString_(Vec3d vec) {
       return  "x: %.4f, y: %.4f, z: %.4f".formatted(vec.x,vec.y,vec.z);

    }


    public static boolean canLoadAi(int id,int count,int max){
        return (fastHash(id) %(count+1)) <= max;
    }

    private static final boolean debug = true;

    public static void rDEBUG(String message)
    {
        if(debug) LogUtils.getLogger().info("[ROF]"+message);

    }
    /**
     * text: 快速的生成一个Text
     *
     * @param string 被解析的字符串。&x为表示更改格式，{}表示应用styleUpdaters 参数。应用顺序以}为准。
     * @param styleUpdaters 格式
     * @return 生成的文本
     */
    @SafeVarargs
    public static MutableText text(String string, UnaryOperator<Style> ... styleUpdaters){
        int len = string.length();
        int updaterIndex = 0;

        Deque< MutableText> textStack = new ArrayDeque<>();
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
                    // updated = ...
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
                Style baseStyle = styleStack.pop();
                if (updaterIndex < styleUpdaters.length) {
                    UnaryOperator<Style> updater = styleUpdaters[updaterIndex++];
                    inner = inner.styled(updater);
                }
                textStack.peek().append(inner);
                i++;
                continue;
            }
            Text literal = Text.literal(String.valueOf(ch))
                    .styled(s -> styleStack.peek());
            textStack.peek().append(literal);
            i++;
        }

        return textStack.pop();
    }

    @SafeVarargs
    public static Supplier<Text> textS(String string, UnaryOperator<Style> ... styleUpdaters){
        return  ()->text(string,styleUpdaters);
    }


    public static MutableText processDisplay(String taskName,double progress)
    {
        // 防御式处理
        if (Double.isNaN(progress)) progress = 0.0;
        progress = Math.max(0.0, Math.min(1.0, progress));

        final int barWidth = 20; // 进度条长度（字符数）
        int filled = (int) Math.round(progress * barWidth);

        MutableText text = Text.empty();


        // 任务名
        if(progress >= 1.0) {
            text.append(Text.literal(taskName + " ")
                    .formatted(Formatting.GREEN));

        }else {
            text.append(Text.literal(taskName + " ")
                    .formatted(Formatting.GRAY));

        }

        // 左括号
        text.append(Text.literal("[")
                .formatted(Formatting.DARK_GRAY));

        // 已完成部分
        if (filled > 0) {
            text.append(Text.literal("▇".repeat(filled))
                    .formatted(Formatting.GREEN));
        }

        // 未完成部分
        int empty = barWidth - filled;
        if (empty > 0) {
            text.append(Text.literal("▇".repeat(empty))
                    .formatted(Formatting.DARK_GRAY));
        }

        // 右括号
        text.append(Text.literal("] ")
                .formatted(Formatting.DARK_GRAY));

        // 百分比
        text.append(Text.literal(String.format("%5.1f%%", progress * 100))
                .formatted(Formatting.YELLOW));

        return text;
    }

    public static void saveNBT2Data(ServerWorld world, String fileName, NbtCompound nbtCompound)
    {
        try {
            Path savaPath = RofTool.getSavePath(world).resolve("data").resolve(fileName);
            if (!savaPath.toFile().exists()) {
                savaPath.toFile().createNewFile();
            }
            NbtIo.writeCompressed(nbtCompound, savaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

