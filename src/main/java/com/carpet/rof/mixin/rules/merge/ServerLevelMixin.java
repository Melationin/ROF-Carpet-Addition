package com.carpet.rof.mixin.rules.merge;

import com.carpet.rof.rules.merge.EntityTickOrderAccessor;
import com.carpet.rof.rules.merge.MergeSetting;
import com.carpet.rof.rules.merge.MergedEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static com.carpet.rof.rules.merge.MergeSetting.MergeTNTNextMode.ALMOST_VANILLA;
import static com.carpet.rof.rules.merge.MergeSetting.MergeTNTNextMode.SAFE_PLUS;
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
        if (mergeTNTNext == SAFE_PLUS)
        {
            PrimedTnt[] leader = new PrimedTnt[1];
            MergeSetting.EntityPosAndVec[] leaderKey = new MergeSetting.EntityPosAndVec[1];
            this.entityTickList.forEach(entity -> {
                if (!rof$willTick(entity)) return;
                if (!(entity instanceof PrimedTnt tnt) || tnt.getFuse() != 1)
                {
                    leader[0] = null;
                    return;
                }
                MergeSetting.EntityPosAndVec key = new MergeSetting.EntityPosAndVec(
                        new Vec3(tnt.getX(), tnt.getY(), tnt.getZ()), tnt.getDeltaMovement(), tnt.getFuse());
                if (leader[0] != null && key.equals(leaderKey[0]))
                {
                    ((MergedEntityAccessor) leader[0]).ROF$addMergeCount(1);
                    tnt.remove(Entity.RemovalReason.DISCARDED);
                }
                else
                {
                    leader[0] = tnt;
                    leaderKey[0] = key;
                }
            });
            return;
        }
        if (mergeTNTNext != ALMOST_VANILLA) return;
        AtomicLong tickOrder = new AtomicLong();
        this.entityTickList.forEach(entity -> {
            if (rof$willTick(entity))
            {
                ((EntityTickOrderAccessor)(Object)entity).rof$setTickOrder(tickOrder.get());
                tickOrder.getAndIncrement();
            }
            else
            {
                ((EntityTickOrderAccessor)(Object)entity).rof$setTickOrder(-1);
            }
        });
    }

    @Unique
    private boolean rof$willTick(Entity entity)
    {
        ServerLevel level = (ServerLevel) (Object) this;
        return !entity.isRemoved()
                && !level.tickRateManager().isEntityFrozen(entity)
                && (entity instanceof ServerPlayer || level.isPositionEntityTicking(entity.blockPosition()));
    }
}
