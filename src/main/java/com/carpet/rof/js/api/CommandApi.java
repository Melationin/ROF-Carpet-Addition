package com.carpet.rof.js.api;

import com.carpet.rof.rules.playerJScript.JScriptContext;
import com.carpet.rof.rules.playerJScript.JsExecution;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public final class CommandApi extends ScriptableObject {

    public void jsFunction_command(String cmd) {
        var ctx = JsExecution.get();
        if (ctx.player != null) {
            ctx.server.execute(() -> {
                ctx.server.getCommandManager().parseAndExecute(ctx.player.getCommandSource(),cmd);
            });
        }

    }

    @Override
    public String getClassName() {
        return "command";
    }
}