package com.carpet.rof.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.world.World;

import java.util.HashMap;

public class PacketRecord{
    /*
    PacketRecord() {}
    public final static PacketRecord packetRecord = new PacketRecord();
    public static boolean record = false;
    public static int packetCountPerTick = 0;
    public final static  HashMap<PacketType<?>, Integer> PacketMap = new HashMap<>();

    public static void  addPacket(Packet<?> packet) {
        if(!record){
            return;
        }

        //System.out.println(packet.toString());
       if(PacketMap.containsKey(packet.getPacketType())){
           PacketMap.put(packet.,1+PacketMap.get(packet.getPacketType()));
       }else {
           PacketMap.put(packet.getPacketType(),1);
       }
    }

    public static void clearPacket() {
        PacketMap.clear();
    }

    public static void print(){
        for(var entry : PacketMap.entrySet() ){
            System.out.println(entry.getKey().toString() +" : "+ entry.getValue().toString());
        }
    }
*/
}
