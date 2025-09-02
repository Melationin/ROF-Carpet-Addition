package com.carpet.rof.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.*;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.StorageKey;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.carpet.rof.ROFCarpetSettings.ChunkUpdateHighInterval;

public class HighChunkSet {



    private final Set<Long> highChunkSet = ConcurrentHashMap.newKeySet();
    private final HashMap<Long,Integer> chunkHighestBlockPosMap = new HashMap<>();

    public int topY;

    public ServerWorld world;

    private long chunkCache;
    private boolean chunkCacheValue;

    public HighChunkSet(int topY,ServerWorld world) {
        this.topY = topY;
        this.world = world;
    }

    public boolean get(ChunkPos chunkPos) {

        if(chunkPos.toLong()==chunkCache){
            return chunkCacheValue;
        }
        if(highChunkSet.contains(chunkPos.toLong())){
            chunkCacheValue = true;
            chunkCache = chunkPos.toLong();
            return true;

        }else {
            chunkCacheValue = false;
            chunkCache = chunkPos.toLong();
            return false;

        }



    }

    public void add(ChunkPos chunkPos) {
       if( highChunkSet.add(chunkPos.toLong())) {
           System.out.println("Add chunk pos :" + chunkPos.toString());
           if(chunkCache == chunkPos.toLong()){
              chunkCacheValue = true;
           }
       }
    }

    public void remove(ChunkPos chunkPos) {
        if(highChunkSet.remove(chunkPos.toLong())) {
            System.out.println("Removed chunk pos " + chunkPos.toString());
            chunkHighestBlockPosMap.remove(chunkPos.toLong());
            if(chunkCache == chunkPos.toLong()){
                chunkCacheValue = false;
            }
        }
    }

    public int size() {
        return highChunkSet.size();
    }

    public void SaveToFile(Path path){
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toString()))) {
            oos.writeInt(highChunkSet.size());
            for(Long l : highChunkSet) {
                oos.writeLong(l);
            }
        } catch (IOException e) {

        }

    }

    public void LoadFromFile(Path path){
        try (ObjectInputStream ios = new ObjectInputStream(
                new FileInputStream(path.toString()))) {
            highChunkSet.clear();
            for(int i = ios.readInt();i>0;i--){
                highChunkSet.add(ios.readLong());
            }

        } catch (IOException e) {
        }
    }



    public void Save() {
        Path savaPath = RofTool.getSavePath(world).resolve("data").resolve("highChunkSet.dat");;
        try {
            if(!savaPath.toFile().exists()) {
                savaPath.toFile().createNewFile();
            }
            SaveToFile(savaPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //ServerChunkManager serverChunkManager = world.getChunkManager();

    public boolean load() {
        Path savaPath = RofTool.getSavePath(world).resolve("data").resolve("highChunkSet.dat");
        if(savaPath.toFile().exists()) {
                LoadFromFile(savaPath);
                return true;
        }
        return false;
    }


    private  long getHighest(long data,int length){
        long Res = -1000;
        for(int i=0;i<64 - length ;i+=length) {
            long s1  = data&((1L <<length)- 1);
            if(s1>Res) Res = s1;
            data >>= length;
        }
        return Res;
    }


    public void LoadFromRegion(Path RegionFileFolder, int x,int y) {
        String currentRegionName = "r." + x + "." + y + ".mca";
        Path RegionFilePath = RegionFileFolder.resolve(currentRegionName);
        try {
            RegionFile test = new RegionFile(new StorageKey("string1",world.getRegistryKey(),"string2"), RegionFilePath,RegionFileFolder,false);
            for(int i = 0;i<32;i++)
                for(int j = 0;j<32;j++){
                    DataInputStream dataInputStream = test.getChunkInputStream(new ChunkPos((x<<5 )+ i,(y<<5) + j));
                    if(dataInputStream != null) {
                        NbtCompound chunkDate = NbtIo.readCompound(dataInputStream);
                        int finalJ = chunkDate.getInt("xPos").get();
                        int finalI = chunkDate.getInt("zPos").get();;
                        chunkDate.getCompound("Heightmaps").flatMap(heightmaps -> heightmaps.getLongArray(Heightmap.Type.MOTION_BLOCKING.getId())).ifPresent(heightmap -> {
                            for (long l : heightmap) {
                                if (getHighest(l, 9) + world.getBottomY() > topY) {
                                    add(new ChunkPos(finalJ,finalI));
                                    System.out.println("l : "+l);
                                    break;
                                }
                            }
                        });
                    }
                }
            test.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void clear() {
        highChunkSet.clear();
    }

    public static class ReloadThread extends Thread{
        HighChunkSet highChunkSet;
        CommandContext<ServerCommandSource> context;
        public ReloadThread(HighChunkSet highChunkSet, CommandContext<ServerCommandSource> context) {
            this.highChunkSet = highChunkSet;
            this.context = context;
        }

        public void run() {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if(  player  != null) player.sendMessage(Text.of("Loading highChunkSet from region file..."));
            highChunkSet.reload();
            if(  player  != null)  player.sendMessage(Text.of("Finished loading"));
        }

    }

    public boolean reload(){
        if(world==null) return false;
        try {
            Path regionsFolder = RofTool.getSavePath(world).resolve("region");
            File folder =  regionsFolder.toFile();
            if (folder.isDirectory() ) {
                for(String fileName : folder.list()) {
                    String[] split = fileName.split("\\.");
                    if(split.length ==4 && split[0].equals("r") && split[3].equals("mca")) {
                        LoadFromRegion(regionsFolder,Integer.parseInt(split[1]),Integer.parseInt(split[2]));

                    }
                }
            }
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public void update() {
        outerLoop:
        for(Long l : highChunkSet){
            if((world.getTime()+l)%ChunkUpdateHighInterval == 0) {
                ChunkPos chunkPos = new ChunkPos(l);
                Chunk chunk = world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
                if(chunk != null) {
                    Heightmap hmp =  chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
                    int maxPos = chunkHighestBlockPosMap.getOrDefault(chunkPos.toLong(),0);
                    if((hmp.get(maxPos/16,maxPos%16) >= topY)) continue;
                    for(int i = 0;i<256;i++)
                        if(hmp.get(i/16,i%16) >= topY){
                            chunkHighestBlockPosMap.put(l,i);
                            continue outerLoop;
                        }
                    remove(chunk.getPos());
                }
            }
        }
    }

}
