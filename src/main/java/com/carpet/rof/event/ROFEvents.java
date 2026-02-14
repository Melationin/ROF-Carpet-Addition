package com.carpet.rof.event;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ROFEvents
{






    public static class Event<T>{
        private  List<Consumer<T>> backCalls = new ArrayList<>();
        public  void register(Consumer<T> backCall){
            backCalls.add(backCall);
        }

        public  void run(T owner){
            for(Consumer<T> backCall : backCalls){
                backCall.accept(owner);
            }
        }
    }


    public static class task<T>{
        private int tick = 0;
        private final BiFunction<T,Integer,Boolean> backCall;

        public task(BiFunction<T, Integer, Boolean> backCall)
        {
            this.backCall = backCall;
        }

        public boolean apply(T owner)
        {
            return backCall.apply(owner,tick++);
        }
    }

    //可以多回调。返回为
    public static class tickTasks<T>{
        private final Set<task<T>> backCalls = new HashSet<>();
        public  void register(Function<T,Boolean> backCall){
            backCalls.add(new task<>((t,tick)->backCall.apply(t)));

        }
        public  void register(BiFunction<T,Integer,Boolean> backCall){
            backCalls.add(new task<>(backCall));
        }

        public void run(T owner){
            backCalls.removeIf(backCall -> backCall.apply(owner));
        }
    }



    public static Event<MinecraftServer> ServerStart = new Event<>() ;
    public static Event<MinecraftServer> ServerTickBegin = new Event<>();
    public static Event<MinecraftServer> ServerTickEnd  = new Event<>();

    public static tickTasks<MinecraftServer> ServerTickEndTasks = new tickTasks<>();
    public static tickTasks<ServerWorld> WorldTickEndTasks= new tickTasks<>();

    public static Event<ServerWorld> WorldStart= new Event<>();
    public static Event<ServerWorld> WorldTickBegin= new Event<>();
    public static Event<ServerWorld> WorldTickEnd= new Event<>();


}
