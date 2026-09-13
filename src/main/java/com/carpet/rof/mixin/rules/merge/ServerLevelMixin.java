package com.carpet.rof.mixin.rules.merge;

import com.carpet.rof.extraWorldData.ExtraWorldDatas;
import com.carpet.rof.rules.merge.EntityTickOrderAccessor;
import com.carpet.rof.rules.merge.MergeSetting;
import com.carpet.rof.utils.ROFWarp;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static com.carpet.rof.rules.merge.MergeSetting.MergeTNTNextMode.ALMOST_VANILLA;
import static com.carpet.rof.rules.merge.MergeSetting.mergeTNTNext;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Shadow
    @Final
    private EntityTickList entityTickList;

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void onTick(final BooleanSupplier haveTime, final CallbackInfo ci)
    {
        if (mergeTNTNext != ALMOST_VANILLA) return;
        AtomicLong tickOrder = new AtomicLong();
        this.entityTickList.forEach(entity -> {
            if (!entity.isRemoved()) {
                ((EntityTickOrderAccessor)(Object)entity).rof$setTickOrder(tickOrder.get());
                tickOrder.getAndIncrement();

            }
        });
    }
}
