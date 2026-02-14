package com.carpet.rof.commands.chunkFilter;

import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.RofTool;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class ChunkFilterThread
{
    ChunkFilter chunkFilter;
    ServerCommandSource serverCommandSource;
    CompletableFuture<Void> task = null;
    CompletableFuture<Void> feedback = null;

    public ChunkFilterThread(ServerWorld world)
    {
        this.chunkFilter = new ChunkFilter(world);

    }

    public double getProcess()
    {
        return this.chunkFilter.process;
    }

    public void asyncFunc(Runnable runnable,String name, ServerCommandSource serverCommandSource)
    {

        if (task != null && task.isDone()) {
            task.cancel(true);
        }
        if (feedback != null && !feedback.isDone()) {
            feedback.cancel(true);
        }
        this.serverCommandSource = serverCommandSource;
        chunkFilter.process = 0;
        chunkFilter.changeCount = 0;
        task = CompletableFuture.runAsync(() ->
        {
            runnable.run();
            chunkFilter.process = 1;
        });
        ROFEvents.ServerTickEndTasks.register((server, tick)->{
            if(serverCommandSource.getPlayer() instanceof ServerPlayerEntity player){
                if(player.isDisconnected()){
                    return true;
                }else if(chunkFilter.process >= 1){
                    player.sendMessage(RofTool.processDisplay("[ChunkFilter]"+name,chunkFilter.process),true);
                    player.sendMessage(RofTool.text("[ChunkFilter]任务"+name+"完成！"),false);
                    return true;
                }
                player.sendMessage(RofTool.processDisplay("[ChunkFilter]"+name,chunkFilter.process),true);
                return false;
            }else {
                if(chunkFilter.process >= 1){
                    serverCommandSource.sendFeedback(()->RofTool.processDisplay("[ChunkFilter]"+name,chunkFilter.process),false);
                    serverCommandSource.sendFeedback(()->RofTool.text("[ChunkFilter]任务"+name+"完成！"),false);
                    return true;
                }
                if(tick % 20 == 0){
                    serverCommandSource.sendFeedback(()->RofTool.processDisplay("[ChunkFilter]"+name,chunkFilter.process),false);
                }
                return false;
            }
        });


    }

    public void close()
    {
        if (task != null && !task.isDone()) {
            task.cancel(true);
            task = null;
        }
        if (feedback != null && !feedback.isDone()) {
            feedback.cancel(true);
            feedback = null;
        }
    }

}
