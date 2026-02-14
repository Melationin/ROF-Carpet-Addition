package com.carpet.rof.mixin.autoFreeze;

import com.carpet.rof.utils.ROFWarp;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerTickManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.carpet.rof.rules.autoFreeze.AutoFreezeSettings.*;
import static com.carpet.rof.utils.RofTool.text;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{


    @Shadow
    @Final
    private ServerTickManager tickManager;
    @Unique
    private long nanoTime = 0;
    @Unique
    private long highLogTick = 0;

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick_Head(CallbackInfo ci)
    {
        this.nanoTime = Util.getMeasuringTimeNano();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick_Tail(CallbackInfo ci)
    {
        this.nanoTime = Util.getMeasuringTimeNano() - this.nanoTime;
        if (this.tickManager.shouldTick()) {
            if (nanoTime / 1000000.0 > highLagFreezeLimit && highLagFreezeLimit > 0) {
                highLogTick++;
                if (highLogTick > highLagFreezeTickLimit) {
                    this.tickManager.setFrozen(true);
                    this.getPlayerManager().getPlayerList().forEach(player ->
                    {
                        player.sendMessage(text("{&c因为游戏剧烈卡顿，现已暂停}",
                                style -> style.withHoverEvent(ROFWarp.showText(text("过去 1 tick 用时大于"+nanoTime / 1000000.0 +"毫秒")))
                                )
                        );
                    });
                    this.highLogTick = 0;
                }
            } else {
                highLogTick = 0;
            }
        }
    }


}
