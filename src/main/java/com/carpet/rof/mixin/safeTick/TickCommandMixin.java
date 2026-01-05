package com.carpet.rof.mixin.safeTick;

import com.carpet.rof.commands.SafeTickCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TickCommand;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    @Inject(method = "executeRate",at = @At(value = "HEAD"))
    private static void executeRate(ServerCommandSource source, float rate, CallbackInfoReturnable<Integer> cir){
        if(SafeTickCommand.SafeTickRate !=-1) {
            SafeTickCommand.SafeTickRate = -1;
            source.sendFeedback(()-> Text.of("Stop Safe Tick"),true);
        }
    }

    @Inject(method = "executeSprint",at = @At(value = "HEAD"))
    private static void executeSprint(ServerCommandSource source, int ticks, CallbackInfoReturnable<Integer> cir){
        if(SafeTickCommand.SafeTickRate !=-1) {
            SafeTickCommand.SafeTickRate = -1;
            source.sendFeedback(()-> Text.of("Stop Safe Tick"),true);
        }
    }
}
