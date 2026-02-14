package com.carpet.rof.logger.packetLogger;

import com.carpet.rof.utils.ROFWarp;
import com.carpet.rof.utils.singleTaskWorker.ROFTask;
import com.carpet.rof.utils.singleTaskWorker.SPSCRingBuffer;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketLogger {

    public final Map<PacketType<?>,Long> packetCountMap = new HashMap<>();
    public final Map<PacketType<?>,Long> packetSizeMap = new HashMap<>();
    public static PacketLogger instance = null;
    public boolean isRunning = false;
    private long startTime;
    private long endTime;
    private long realStartTime;
    private long realEndTime;
    public class PacketLoggerTask implements ROFTask{
        public enum Type{
            ADD,
            CLEAR
        }

        Type type;
        Packet<?> packet;
        ByteBuf byteBuf;

        @Override
        public void run() {
            if(type == Type.ADD){
                packetCountMap.merge(ROFWarp.getPacketType(packet), 1L, Long::sum);
                packetSizeMap.merge(ROFWarp.getPacketType(packet), (long) byteBuf.readableBytes(), Long::sum);
            }else if(type == Type.CLEAR){
                packetCountMap.clear();
                packetSizeMap.clear();
            }
        }

        public PacketLoggerTask(Packet<?> packet,Type  type, ByteBuf byteBuf) {
            this.type = type;
            this.packet = packet;
            this.byteBuf = byteBuf;
        }
    }

    public final SPSCRingBuffer<PacketLoggerTask> ringBuffer = new SPSCRingBuffer<>(10000);

    public void addPacket(Packet<?> packet,ByteBuf byteBuf){
        if(isRunning){
            ringBuffer.offer(new PacketLoggerTask(packet, PacketLoggerTask.Type.ADD,byteBuf));
        }
    }

    public void addPacket(PacketLoggerTask task){
        if(isRunning){
            ringBuffer.offer(task);
        }
    }

    public void clear(){
        ringBuffer.offer(new PacketLoggerTask(null,PacketLoggerTask.Type.CLEAR,null));
    }

    public void start(long startTime){
        this.realStartTime = System.currentTimeMillis();
        this.startTime = startTime;
        isRunning = true;
        SingleTaskWorker.INSTANCE.addBuffer("PacketLogger",ringBuffer);
        clear();

    }

    public void stop(long endTime){
        this.endTime = endTime;
        this.realEndTime = System.currentTimeMillis();
        isRunning = false;
        SingleTaskWorker.INSTANCE.removeBuffer("PacketLogger");
    }

    public long getTicks(){
        return endTime - startTime;
    }

    public long getRealTime(){
        return realEndTime - realStartTime;
    }

    public void setEndtime(long endTime){
        this.endTime = endTime;
        this.realEndTime = System.currentTimeMillis();

    }
}
