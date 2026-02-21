package com.carpet.rof.utils.singleTaskWorker;

import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

public class SingleTaskWorker {
    private final Map<String,SPSCRingBuffer<?>> BUFFERS = new ConcurrentHashMap<>();
    private Thread consumerThread;
    private volatile boolean running = true;

    public static SingleTaskWorker INSTANCE = new SingleTaskWorker();


    public SingleTaskWorker get(ServerWorld world)
    {
        return INSTANCE;
    }

    public void start(){
        running = true;
        consumerThread = new Thread(()->{
            while (running){
                boolean wait  = true;
                for(var buffer : BUFFERS.values()){
                    var it = buffer.poll();
                    if(it!=null){
                        wait  = false;
                        it.run();
                    }
                }
                if(wait){

                    LockSupport.parkNanos(1_000_000 * 10);
                }
                if (!running) {
                    break;
                }
            }
        });

        consumerThread.start();
    }

    public void addBuffer(String name,int size){
        if(!running) {
            BUFFERS.put(name, new SPSCRingBuffer<>(size));
        }
    }

    public void addBuffer(String name,SPSCRingBuffer<?> buffer){
        BUFFERS.put(name, buffer);
    }

    public void removeBuffer(String name){
        BUFFERS.remove(name);
    }


    public void stop(){
        running = false;
        if(consumerThread!=null){
            LockSupport.unpark(consumerThread);
        }
    }

}
