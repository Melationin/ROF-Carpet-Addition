package com.carpet.rof;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.carpet.rof.command.RequirementModifyCommand;
import com.carpet.rof.event.ROFEvents;
import com.carpet.rof.world.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.utils.AutoMixinAuditExecutor;
import com.carpet.rof.utils.ROFConfig;
import com.carpet.rof.carpet.composite.CompositeRuleManager;
import com.carpet.rof.utils.ROFCarpetTranslations;
import com.carpet.rof.utils.ROFTool;
import com.carpet.rof.utils.singleTaskWorker.SingleTaskWorker;
import com.carpet.rof.world.chunkTick.async.AsyncExecutor;
import com.carpet.rof.entity.oec.OecUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.storage.LevelResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.carpet.rof.command.RequirementModifyCommand.requirementModifyMap;

public class ROFCarpetServer implements CarpetExtension, ModInitializer
{
    public static void loadExtension()
    {
        CarpetServer.manageExtension(new ROFCarpetServer());
    }



    @Override
    public String version()
    {
        return "ROF";
    }

    @Override
    public void onInitialize()
    {

        ROFCarpetServer.loadExtension();
        AutoMixinAuditExecutor.run();

    }

    @Override
    public void onGameStarted()
    {
        ROFSettings.loadClasses();
        for (Class<?> r : ROFSettings.ruleClasses) {
            CarpetServer.settingsManager.parseSettingsClass(r);
        }

        ROFEvents.WorldTickBegin.register(world -> {
            ExtraWorldDatas.fromWorld(world).entitySpawnCountsPerTick.clear();
            ExtraWorldDatas.fromWorld(world).mergeTntMap.clear();
            ExtraWorldDatas.fromWorld(world).mergeTntMapAv.clear();
            ExtraWorldDatas.fromWorld(world).chunkEntitySpawnLogger.run(world);
            ExtraWorldDatas.fromWorld(world).killedTriggerLimiter.clear();
        });

        ROFEvents.ServerSave.register(server2 -> {
            ROFConfig.INSTANCE.set("requirementModifyMap", requirementModifyMap);
            ROFConfig.INSTANCE.save();
        });
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        OecUtil.releaseAllGrids();
        AsyncExecutor.stop();
        SingleTaskWorker.INSTANCE.stop();
    }
    @Override
    public void onServerLoaded(MinecraftServer server)
    {
        initOnServer(server);
        ROFEvents.ServerStart.run(server);
    }

    public void initOnServer(MinecraftServer server){
        SingleTaskWorker.INSTANCE.start();
        AsyncExecutor.start();
        ROFConfig.INSTANCE = new ROFConfig(server.getWorldPath(LevelResource.ROOT).resolve("carpet-rof-addition.json"));
        ROFConfig.INSTANCE.load();
        CompositeRuleManager.of(CarpetServer.settingsManager).load(ROFConfig.INSTANCE);
        RequirementModifyCommand.initialization(server,ROFConfig.INSTANCE);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        var map = ROFCarpetTranslations.getTranslationFromResourcePath(lang);


        /*
        StringBuilder builder = new StringBuilder();

        map.forEach((string, string2) -> {
            builder.append("\"").append(string).append("\" : \"").append(string2).append("\",\n");
        });
        System.out.println(builder.toString());

         */
        return map;
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext)
    {
        ROFCommands.register(dispatcher);
    }

    @Override
    public void registerLoggers()
    {
        ROFLoggers.register();
    }
}
