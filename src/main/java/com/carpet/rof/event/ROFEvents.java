package com.carpet.rof.event;

import com.carpet.rof.utils.Pair;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ROFEvents
{
    /** 非热点数据，提供给事件回调使用，key 是一个字符串，value 是一个 Object，可以是任何类型的数据。事件回调可以通过这个 map 来存储和获取数据*/
    private static Map<Pair<String,Object>,Object> data = new HashMap<>();

    public static  <T> T getData(String key, Object owner, T defaultValue) {
        if(data.get(new Pair<>(key, owner)) != null){
            return (T)data.get(new Pair<>(key, owner));
        }
        return defaultValue;
    }
    public static  <T> T getData(String key, Object owner) {
        if(data.get(new Pair<>(key, owner)) != null){
            return (T)data.get(new Pair<>(key, owner));
        }
        return null;
    }

    public static  <T> T putData(String key, Object owner,T value) {
        data.put(new Pair<>(key, owner), value);
        return null;
    }
    public static class Event<T>
    {
        private final List<Consumer<T>> backCalls = new ArrayList<>();

        public void register(Consumer<T> backCall) {
            backCalls.add(backCall);
        }

        public void run(T owner) {
            for (Consumer<T> backCall : backCalls) {
                backCall.accept(owner);
            }
        }
    }

    public static class Task<T>
    {
        private int tick = 0;
        private final BiFunction<T, Integer, Boolean> backCall;

        public Task(BiFunction<T, Integer, Boolean> backCall) {
            this.backCall = backCall;
        }

        public boolean apply(T owner) {
            return backCall.apply(owner, tick++);
        }
    }

    /** 可以多回调，返回 true 时自动移除。 */
    public static class TickTasks<T>
    {
        private final Set<Task<T>> backCalls = new HashSet<>();

        public void register(Function<T, Boolean> backCall) {
            backCalls.add(new Task<>((t, tick) -> backCall.apply(t)));
        }

        public void register(BiFunction<T, Integer, Boolean> backCall) {
            backCalls.add(new Task<>(backCall));
        }

        public void run(T owner) {
            backCalls.removeIf(backCall -> backCall.apply(owner));
        }
    }

    public static final Event<MinecraftServer> ServerStart     = new Event<>();
    public static final Event<MinecraftServer> ServerTickBegin = new Event<>();
    public static final Event<MinecraftServer> ServerTickEnd   = new Event<>();

    public static final TickTasks<MinecraftServer> ServerTickEndTasks = new TickTasks<>();
    public static final TickTasks<ServerWorld>     WorldTickEndTasks  = new TickTasks<>();

    public static final Event<ServerWorld> WorldStart     = new Event<>();
    public static final Event<ServerWorld> WorldTickBegin = new Event<>();
    public static final Event<ServerWorld> WorldTickEnd   = new Event<>();
}
