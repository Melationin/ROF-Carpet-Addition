package com.carpet.rof.utils;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockEventPreQueue {

    public static class preBlockEvent implements Serializable {
        public int x;
        public int y;
        public int z;
        public int type;
        public int data;
        preBlockEvent(BlockEvent blockEvent){
            x = blockEvent.pos().getX();
            y = blockEvent.pos().getY();
            z = blockEvent.pos().getZ();
            type = blockEvent.type();
            data = blockEvent.data();

        }
    }


    public ObjectLinkedOpenHashSet<preBlockEvent> getPreBlockEventQueue() {
        return preBlockEventQueue;
    }

    private ObjectLinkedOpenHashSet<preBlockEvent> preBlockEventQueue = new ObjectLinkedOpenHashSet<>();

    public void SaveToFile(Path path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toString()))) {
            oos.writeObject(preBlockEventQueue);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }




    @SuppressWarnings("unchecked")
    public void loadFromFile(Path file)
            {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file.toString()))) {
            preBlockEventQueue = (ObjectLinkedOpenHashSet<preBlockEvent>)ois.readObject();

            ois.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load(ServerWorld world) {
        Path savaPath = RofTool.getSavePath(world).resolve("data").resolve("preBEqueue.dat");
        if(savaPath.toFile().exists()) {
            loadFromFile(savaPath);


        }else {
            preBlockEventQueue.clear();
        }
    }

    public void loadFromBEqueue(ServerWorld world) {
        for(var blockEvent : world.syncedBlockEventQueue) {
            preBlockEventQueue.add(new preBlockEvent(blockEvent));
        }
    }


    public void Save(ServerWorld world) {
        Path savaPath = RofTool.getSavePath(world).resolve("data").resolve("preBEqueue.dat");
        try {
            if(!savaPath.toFile().exists()) {
                savaPath.toFile().createNewFile();
            }
            SaveToFile(savaPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
