package com.carpet.rof.mixin.safeTick;

import carpet.patches.EntityPlayerMPFake;
import com.carpet.rof.commands.SafeTickCommand;
import com.carpet.rof.commands.SearchCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public abstract class ServerMixin {

    @Shadow public abstract PlayerManager getPlayerManager();

    @Shadow public abstract ServerTickManager getTickManager();

    @Shadow public abstract ServerCommandSource getCommandSource();

    @Shadow public abstract void sendMessage(Text message);

    @Inject(method = "loadWorld",at = @At(value = "HEAD"))
    private static void startServer(CallbackInfo ci){
        SafeTickCommand.SafeTickRate = -1;
    }

    @Inject(method = "tick",at = @At(value = "HEAD"))
    private void tickServer(CallbackInfo ci){



       var playerList =   this.getPlayerManager().getPlayerList();
       int survivalRealPlayerCount = 0;
       for (var player : playerList){
           if(!player.isSpectator()
           && !(player instanceof EntityPlayerMPFake)){
               survivalRealPlayerCount++;
           }
       }

       if(survivalRealPlayerCount == 0){
           if(this.getTickManager().shouldTick()){
               if(!this.getTickManager().isSprinting()
               &&false){
                   this.getTickManager().startSprint(20*60*60*365);
                   this.sendMessage(Text.of("Start tick sprint!!!"));
               } else if (SafeTickCommand.SafeTickRate >=0 && this.getTickManager().getTickRate()!=SafeTickCommand.SafeTickRate) {
                   this.getTickManager().setTickRate(SafeTickCommand.SafeTickRate);
               }
           }
       }
       else {
           if(SafeTickCommand.SafeTickRate != -1){
               if(this.getTickManager().shouldTick()){
                   if(this.getTickManager().isSprinting()){
                    this.getTickManager().stopSprinting();
                    this.getCommandSource().sendFeedback(()-> Text.of("Stop tick sprint!!!"),true);
                   }
                   if(this.getTickManager().getTickRate()!=20){
                       this.getTickManager().setTickRate(20);
                       this.getCommandSource().sendFeedback(()-> Text.of("Set tick rate to 20"),true);
                   }
               }
           }
       }
    }
}
