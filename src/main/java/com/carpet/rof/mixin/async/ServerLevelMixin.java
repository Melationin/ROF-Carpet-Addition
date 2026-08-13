package com.carpet.rof.mixin.async;

import com.carpet.rof.utils.asyncWorldgen.randomTick.AsyncRandomTick;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = ServerLevel.class,
       priority = 900)
public class ServerLevelMixin
{
    @Inject(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void rof$asyncRandomTick(LevelChunk chunk, int speed, CallbackInfo ci)
    {
        AsyncRandomTick.collect(chunk, speed);
        if (AsyncRandomTick.consume((ServerLevel) (Object) this, chunk))
            ci.cancel();
    }
}
