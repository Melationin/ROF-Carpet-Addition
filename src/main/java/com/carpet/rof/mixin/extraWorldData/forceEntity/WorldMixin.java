package com.carpet.rof.mixin.extraWorldData.forceEntity;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(World.class)
public class WorldMixin
{
    //? >=1.21.5 {
    @Inject(method = "getEntity(Ljava/util/UUID;)Lnet/minecraft/entity/Entity;", at = @At(value = "HEAD"),
            cancellable = true)
    private void getEntityInject(UUID uuid, CallbackInfoReturnable<Entity> cir){
        if((Object)this instanceof ServerWorld serverWorld) {
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
