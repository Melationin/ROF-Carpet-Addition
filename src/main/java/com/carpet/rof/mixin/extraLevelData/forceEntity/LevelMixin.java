package com.carpet.rof.mixin.extraLevelData.forceEntity;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Level.class)
public class LevelMixin
{
    //? >=1.21.5 {
    @Inject(method = "getEntity(Ljava/util/UUID;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "HEAD"),
            cancellable = true)
    private void getEntityInject(UUID uuid, CallbackInfoReturnable<Entity> cir){
        if((Object)this instanceof ServerLevel serverWorld) {
            var forcedEntitylist = ExtraWorldDatas.fromWorld(serverWorld).forcedEntitylist;
            Entity entity = forcedEntitylist.get(uuid);
            if(entity != null) {
                cir.setReturnValue(entity);
                cir.cancel();
            }
        }
    }

    //?}
}
