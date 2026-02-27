package com.carpet.rof.mixin.extraWorldData.forceEntity;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{
    @Inject(method = "collectEntitiesByType(Lnet/minecraft/util/TypeFilter;Ljava/util/function/Predicate;Ljava/util/List;I)V", at = @At(value = "HEAD"), cancellable = true)
    private <T extends Entity>void collectEntitiesByTypeInject(TypeFilter<Entity, T> filter, Predicate<Entity> predicate, List<Entity> result, int limit, CallbackInfo ci)
    {
        var forcedEntitylist = ExtraWorldDatas.fromWorld((ServerWorld)(Object)this).forcedEntitylist;
        forcedEntitylist.forEach((UUID,entity)->{
            if(result.size() >= limit) return;
            if(filter.downcast(entity) !=null&& predicate.test(entity)) {
                result.add(entity);
            }
        });
    }
    //? <1.21.5 {
    /*@Inject(method = "getEntity",at = @At(value = "HEAD"),cancellable = true)
    private void getEntityInject(UUID uuid, CallbackInfoReturnable<Entity> cir){
        var forcedEntitylist = ExtraWorldDatas.fromWorld((ServerWorld)(Object)this).forcedEntitylist;
        Entity entity = forcedEntitylist.get(uuid);
        if(entity != null) {
            cir.setReturnValue(entity);
            cir.cancel();
        }
    }
    *///?}
}
