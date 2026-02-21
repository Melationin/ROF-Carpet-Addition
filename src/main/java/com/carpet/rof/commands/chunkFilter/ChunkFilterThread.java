package com.carpet.rof.commands.chunkFilter;

import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.utils.ROFTool;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.CompletableFuture;

import static com.carpet.rof.utils.ROFTextTool.processDisplay;
import static com.carpet.rof.utils.ROFTextTool.text;

public class ChunkFilterThread
{
    ChunkFilter chunkFilter;
    ServerCommandSource serverCommandSource;
    CompletableFuture<Void> task = null;

    public ChunkFilterThread(ServerWorld world)
    {
        this.chunkFilter = new ChunkFilter(world);

    }

    public double getProcess()
    {
        return this.chunkFilter.progress;
    }

    public void asyncFunc(Runnable runnable,String name, ServerCommandSource serverCommandSource)
    {

        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
        this.serverCommandSource = serverCommandSource;
        chunkFilter.progress = 0;
        chunkFilter.changeCount = 0;
        task = CompletableFuture.runAsync(() ->
        {
            runnable.run();
            chunkFilter.progress = 1;
        });
        ROFEvents.ServerTickEndTasks.register((server, tick)->{
            if(serverCommandSource.getPlayer() instanceof ServerPlayerEntity player){
                if(player.isDisconnected()){
                    return true;
                }else if(chunkFilter.progress >= 1){
                    player.sendMessage(processDisplay("[ChunkFilter]"+name,chunkFilter.progress),true);
                    player.sendMessage(text("[ChunkFilter]任务"+name+"完成！"),false);
                    return true;
                }
                player.sendMessage(processDisplay("[ChunkFilter]"+name,chunkFilter.progress),true);
                return false;
            }else {
                if(chunkFilter.progress >= 1){
                    serverCommandSource.sendFeedback(()-> processDisplay("[ChunkFilter]"+name,chunkFilter.progress),false);
                    serverCommandSource.sendFeedback(()-> text("[ChunkFilter]任务"+name+"完成！"),false);
                    return true;
                }
                if(tick % 20 == 0){
                    serverCommandSource.sendFeedback(()-> processDisplay("[ChunkFilter]"+name,chunkFilter.progress),false);
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

    }

}
