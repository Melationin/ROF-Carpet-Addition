package com.carpet.rof.utils;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.StorageKey;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class ROFIO
{
    public static boolean isGzip(Path path) throws IOException {
        if (!Files.isRegularFile(path)) return false;
        try (InputStream in = Files.newInputStream(path)) {
            byte[] header = new byte[2];
            int read = in.read(header);
            return read == 2
                    && (header[0] & 0xFF) == 0x1F
                    && (header[1] & 0xFF) == 0x8B;
        }
    }

    public static void loadFromRegion(Path regionFileFolder, int x, int y, ServerWorld world, Consumer<NbtCompound> action)
    {
        String currentRegionName = "r." + x + "." + y + ".mca";
        Path regionFilePath = regionFileFolder.resolve(currentRegionName);
        try (RegionFile regionFile = new RegionFile(
                new StorageKey("string1", world.getRegistryKey(), "string2"),
                regionFilePath, regionFileFolder, false))
        {
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    DataInputStream dataInputStream = regionFile.getChunkInputStream(
                            new ChunkPos((x << 5) + i, (y << 5) + j));
                    if (dataInputStream != null) {
                        final NbtCompound chunkData = NbtIo.readCompound(dataInputStream);
                        action.accept(chunkData);
                        dataInputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static <T> Map<ChunkPos,T> loadFromRegion(Path regionFileFolder, int x, int y, ServerWorld world, Function<NbtCompound,T> action)
    {
        Map<ChunkPos,T> map = new HashMap<>();

        String currentRegionName = "r." + x + "." + y + ".mca";
        Path regionFilePath = regionFileFolder.resolve(currentRegionName);
        try (RegionFile regionFile = new RegionFile(
                new StorageKey("string1", world.getRegistryKey(), "string2"),
                regionFilePath, regionFileFolder, false))
        {
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    ChunkPos chunkPos = new ChunkPos((x * 32) + i, (y *32) + j);

                    DataInputStream dataInputStream = regionFile.getChunkInputStream(chunkPos);
                    if (dataInputStream != null) {
                        final NbtCompound chunkData = NbtIo.readCompound(dataInputStream);
                        map.put(chunkPos ,action.apply(chunkData));
                        dataInputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return map;
    }

    public static void forEachExistingChunk(ServerWorld world, Consumer<NbtCompound> action, AtomicDouble progress)
    {
        Path regionsFolder = ROFTool.getSavePath(world).resolve("region");
        File folder = regionsFolder.toFile();
        if (!folder.isDirectory()) {
            progress.set(1);
            return;
        }
        String[] files = folder.list((dir, name) -> name.startsWith("r") && name.endsWith(".mca"));
        if (files == null) {
            progress.set(1);
            return;
        }
        int count = 0;
        progress.set(0);
        for (String fileName : files) {
            if (Thread.currentThread().isInterrupted()) return;

            String[] split = fileName.split("\\.");
            if (split.length == 4 && split[0].equals("r") && split[3].equals("mca")) {
                loadFromRegion(regionsFolder, Integer.parseInt(split[1]), Integer.parseInt(split[2]), world, action);
            }
            count++;
            progress.set((double) count / files.length);
        }
        progress.set(1);
    }


    public static <T> CompletableFuture<Map<ChunkPos,T>>
    forEachExistingChunkParallel(ServerWorld world, Function<NbtCompound,T> action, AtomicDouble progress)
    {
        Path regionsFolder = ROFTool.getSavePath(world).resolve("region");
        File folder = regionsFolder.toFile();
        if (!folder.isDirectory()) {
            progress.set(1);
            return CompletableFuture.completedFuture(Map.of());
        }
        String[] files = folder.list((dir, name) -> name.startsWith("r") && name.endsWith(".mca"));
        if (files == null) {
            progress.set(1);
            return CompletableFuture.completedFuture(Map.of());
        }
        AtomicInteger count = new AtomicInteger();
        progress.set(0);

        ArrayList<CompletableFuture<Map<ChunkPos,T>>> futures = new ArrayList<>();

        for (String fileName : files) {
            String[] split = fileName.split("\\.");
            if (split.length == 4 && split[0].equals("r") && split[3].equals("mca")) {
                futures.add(CompletableFuture.supplyAsync(
                        ()->loadFromRegion(regionsFolder, Integer.parseInt(split[1]), Integer.parseInt(split[2]), world, action)
                ).whenComplete((result, throwable) -> {
                    count.incrementAndGet();
                    if(count.get() < files.length)  progress.set((double) count.get() / files.length);
                }));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(
                (v)->
                {
                    Map<ChunkPos, T> resultMap = new HashMap<>();
                    for (CompletableFuture<Map<ChunkPos,T>> future : futures) {
                        resultMap.putAll(future.join());
                    }

                    return resultMap;
                }
        );
    }
}
