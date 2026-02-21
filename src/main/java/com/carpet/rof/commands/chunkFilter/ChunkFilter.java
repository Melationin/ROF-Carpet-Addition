package com.carpet.rof.commands.chunkFilter;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.extraWorldData.extraChunkDatas.ChunkModifyData;
import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.ROFTool;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.StorageKey;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ChunkFilter {

    public static int LOAD = Integer.MAX_VALUE;
    public static int RESERVE = Integer.MAX_VALUE-1;
    public static int SAVE = Integer.MAX_VALUE-2;

    private static final int[] DX = {
            1, -1,  0,  0,
            1,  1, -1, -1
    };
    private static final int[] DZ = {
            0,  0,  1, -1,
            1, -1,  1, -1
    };
    private final ServerWorld world;

    public double progress = 1;

    public int changeCount = 0;

    public ChunkFilter(ServerWorld world) {
        this.world = world;
    }

    public static class ChunkSample{
        int xPos;
        int zPos;
        int inhabitedTime;
        ChunkSample(int xPos,int zPos, int inhabitedTime) {
            this.xPos = xPos;
            this.zPos = zPos;
            this.inhabitedTime = inhabitedTime;
        }
    }

    private final Long2ObjectMap<ChunkSample> allChunks = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<ChunkSample> filteredChunks =  new Long2ObjectOpenHashMap<>();

    private void remove(long chunkPos){
        if(filteredChunks.containsKey(chunkPos)){
            filteredChunks.remove(chunkPos);
            changeCount --;
        }
    }

    private void remove(ObjectIterator<Long2ObjectMap.Entry<ChunkFilter.ChunkSample>> it){
        it.remove();
        changeCount--;
    }

    private void add(ChunkSample v){
        filteredChunks.put(ChunkPos.toLong(v.xPos,v.zPos),v);
        changeCount ++;
    }

    private void loadFromRegion(Path RegionFileFolder, int x, int y) throws IOException {
        String currentRegionName = "r." + x + "." + y + ".mca";
        Path RegionFilePath = RegionFileFolder.resolve(currentRegionName);
        try(RegionFile test = new RegionFile(new StorageKey("string1",world.getRegistryKey(),"string2"), RegionFilePath,RegionFileFolder,false);) {
            for(int i = 0;i<32;i++)
                for(int j = 0;j<32;j++){
                    try (DataInputStream dataInputStream = test.getChunkInputStream(new ChunkPos((x<<5 )+ i,(y<<5) + j))){
                        if(dataInputStream != null) {
                            NbtCompound chunkData = NbtIo.readCompound(dataInputStream);
                            int xPos = ROFWarp.getFromNbt(chunkData.getInt("xPos"));
                            int zPos = ROFWarp.getFromNbt(chunkData.getInt("zPos"));
                            allChunks.put(ChunkPos.toLong(xPos,zPos),new ChunkSample(xPos,zPos,ROFWarp.getFromNbt(
                                    chunkData.getInt("InhabitedTime"))));
                        }
                    }
                }
        }
    }

    public void load() throws IOException {
        Path regionsFolder = ROFTool.getSavePath(world).resolve("region");
        File folder =  regionsFolder.toFile();
        if (folder.isDirectory()) {
            int finishedCount = 0;
            String[] folder2 = folder.list((dir, name) -> name.startsWith("r") && name.endsWith(".mca"));
            if (folder2 == null) {return;}
            for(String fileName : folder2) {
                String[] split = fileName.split("\\.");
                if(split.length ==4 && split[0].equals("r") && split[3].equals("mca")) {
                    loadFromRegion(regionsFolder,Integer.parseInt(split[1]),Integer.parseInt(split[2]));
                    finishedCount++;
                    progress = finishedCount*1.0/folder2.length;
                }
            }
        }
        changeCount = LOAD;
    }




    private String csvLine(long chunk) {

       int xPos = (int) (chunk & 0xFFFFFFFFL);
       int zPos = (int) (chunk>>> 32);

        return  (int)Math.floor(xPos / 32.0 + 0.000000000001) +
                ";" +
                (int)Math.floor(zPos / 32.0 + 0.000000000001) +
                ";" +
               xPos +
                ";" +
                zPos;
    }

    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(ROFTool.getSavePath(world).resolve(fileName + ".csv"), StandardCharsets.UTF_8)) {
            int count = 0;
            for (long key : filteredChunks.keySet()) {
                count++;
                writer.write(csvLine(key));
                writer.newLine();
                progress = count*1.0/ filteredChunks.size();
            }
            changeCount = SAVE;
        }
    }

    public void addRect(int x1,  int z1, int x2,  int z2) {

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int w = Math.abs(x2-x1);
        int h = Math.abs(z2-z1);
        int count = 0;

        if(w*h == 0 || allChunks.isEmpty()) {
            return;
        }
        if(w*h >= allChunks.size()) {
            for(var chunk : allChunks.values()) {
                int x = chunk .xPos;
                int z = chunk .zPos;
                if( x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                    add(chunk );
                }
                count++;
                progress = count*1.0/allChunks.size();
            }
        }else {
            for(int i = minX;i<=maxX;i++) {
                for(int j = minZ;j<=maxZ;j++) {
                    var chunk = allChunks.get(ChunkPos.toLong(i,j));
                    if(chunk != null) {
                        add(chunk);
                    }
                    count++;
                    progress = count*1.0/(w*h);

                }
            }
        }
    }

    public void removeRect(int x1,  int z1, int x2,  int z2) {
        int w = Math.abs(x2-x1);
        int h = Math.abs(z2-z1);
        int count = 0;
        if(w*h == 0 || filteredChunks.isEmpty()) {
            return;
        }
        int allCount = filteredChunks.size();
        if(w*h >= filteredChunks.size()) {
            var it = filteredChunks.long2ObjectEntrySet().iterator();
            while (it.hasNext()) {
               ChunkSample v = it.next().getValue();
                int x = v.xPos;
                int z = v.zPos;
                if(x >= x1 && x <= x2 && z >= z1 && z <= z2) {
                   remove(it);
                }
                count++;
                progress = count*1.0/allCount;
            }
        }else {
            for(int i = Math.min(x1,x2);i<=Math.max(x1,x2);i++) {
                for(int j = Math.min(z1,z2);j<=Math.max(z1,z2);j++) {
                    remove(ChunkPos.toLong(i,j));
                    count++;
                    progress = count*1.0/(w*h);
                }
            }
        }
    }

    public void addByInhabitedTime(int value){
        int count = 0;
        if(allChunks.isEmpty()) {
            return;
        }
        for(var v : allChunks.values()) {
            if(v.inhabitedTime >= value){
                add(v);
                count++;
                progress = count*1.0/allChunks.size();
            }
        }

    }

    public void removeByInhabitedTime(int value) {
        int count = 0;
        var it =  filteredChunks.long2ObjectEntrySet().iterator();
        if(filteredChunks.isEmpty()) {
            return;
        }
        int allCount = filteredChunks.size();
        while (it.hasNext()) {

            ChunkSample v = it.next().getValue();
            if(v.inhabitedTime >= value){
                remove(it);
                count++;
                progress = count*1.0/allCount;
            }
        }
    }
    public void addByModifyTime(int value){
        int count = 0;
        var data = ExtraWorldDatas.fromWorld(world).chunkModifyData;
        if(allChunks.isEmpty()) {
            return;
        }
        for (ChunkModifyData.ChunkData it: data.chunkDataMap.values()) {
            if(it.modifyTime - it.createTime <= value){
                if(allChunks.containsKey(it.chunkPos)) {
                    this.filteredChunks.put(it.chunkPos,allChunks.get(it.chunkPos));
                }
            }
            count++;
            progress = count*1.0/allChunks.size();
        }

    }

    public void removeByModifyTime(int value) {
        int count = 0;
        var data = ExtraWorldDatas.fromWorld(world).chunkModifyData;
        if(filteredChunks.isEmpty()) {
            return;
        }
        int allCount = filteredChunks.size();
        for (ChunkModifyData.ChunkData it: data.chunkDataMap.values()) {

            if(it.modifyTime - it.createTime <= value){
                if(filteredChunks.containsKey(it.chunkPos)) {
                    this.filteredChunks.remove(it.chunkPos);
                }
            }
            count++;
            progress = count*1.0/allCount;
        }
    }
    public void reverse(){

        allChunks.forEach((key, chunk) -> {
            if(filteredChunks.containsKey(key)) {
               remove(key);
            }else {
                add(chunk);
            }

        });
        changeCount = RESERVE;
    }

    private record Node(ChunkSample chunk, long dist) { }

    public void extend(int distance) {

        if(allChunks.isEmpty()) {
            return;
        }
        LongOpenHashSet visited = new LongOpenHashSet();
        Deque<Node> queue = new ArrayDeque<>();

        for (var v : filteredChunks.values()) {
            long key = ChunkPos.toLong(v.xPos, v.zPos);
            visited.add(key);
            queue.add(new Node(v,0));
        }

        int count = 0;
        while (!queue.isEmpty()) {
            var node = queue.poll();
            if (node.dist >= distance) continue;

            int x0 = node.chunk.xPos;
            int z0 = node.chunk.zPos;

            for (int i = 0; i < 8; i++) {
                int x = x0 + DX[i];
                int z = z0 + DZ[i];
                long key = ChunkPos.toLong(x, z);

                if (!visited.add(key)) continue;

                var chunk = allChunks.get(key);
                if (chunk != null) {
                    add(chunk);
                    queue.add(new Node(chunk,node.dist + 1));
                }
            }
            count++;
            progress = count*1.0/allChunks.size();
        }
    }

    public int getLoadedChunks(){
        return allChunks.size();
    }

    public int getFilteredChunks(){
        return filteredChunks.size();
    }

    public void clearChunks(){
        changeCount = filteredChunks.size();
        filteredChunks.clear();
    }
}