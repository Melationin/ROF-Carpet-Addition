package com.carpet.rof.mixin.playerJScript;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.rules.playerJScript.PlayerJScriptManager;
import com.carpet.rof.rules.playerJScript.ServerPlayerEntityAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.playerJScript.PlayerJScriptSetting.commandPlayerJScript;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin implements ServerPlayerEntityAccessor {

    @Shadow public abstract void tick();

    @Unique
    public PlayerJScriptManager scriptManager = null;




    @Inject(method = "tick",at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci){
        if(!commandPlayerJScript.equals("false")  && scriptManager != null){
            scriptManager.tick();
        }
    }

    @Override
    public PlayerJScriptManager getPlayerJScriptManager() {
        return scriptManager;
    }

    @Override
    public void setPlayerJScriptManager(PlayerJScriptManager playerJScriptManager) {
        if((Object)this instanceof EntityPlayerMPFake playerMPFake) {
            if (playerJScriptManager == null) {
                this.scriptManager = new PlayerJScriptManager(playerMPFake);
            }
        }
    }


}
